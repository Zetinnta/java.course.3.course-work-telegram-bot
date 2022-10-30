package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pro.sky.telegrambot.model.Interaction;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.service.InteractionService;
import pro.sky.telegrambot.service.NotificationTaskService;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final InteractionService interactionService;

    private final NotificationTaskService notificationTaskService;

    @Autowired
    private TelegramBot telegramBot;

    public TelegramBotUpdatesListener(InteractionService interactionService, NotificationTaskService notificationTaskService, TelegramBot telegramBot) {
        this.interactionService = interactionService;
        this.notificationTaskService = notificationTaskService;
        this.telegramBot = telegramBot;
    }

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static final Pattern pattern = Pattern.compile("([0-9.:\\s]{16})(\\s)([\\W+|\\w]+)");

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        Collection<Interaction> allInteractions = interactionService.getAllPossibleInteractions();
        boolean sent = false;
        for (Update update : updates) {
            logger.info("Processing update: {}", update);
            if (update.message() != null) {
                String receivedMessage = update.message().text();
                if (receivedMessage.startsWith("/")) {
                    for (Interaction interaction : allInteractions) {
                        if (receivedMessage.equalsIgnoreCase(interaction.getRequest())) {
                            sent = true;
                            responseTelegramBot(update, interaction.getResponse());
                        }
                    } if (!sent) {
                        responseTelegramBot(update, "What do you want?! Illegal command! Use /help for more information.");
                    }
                } else {
                    addTask(update);
                }
            }
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    public void addTask(Update update) {
        String receivedMessage = update.message().text();
        NotificationTask notificationTask = new NotificationTask();
        Matcher matcher = pattern.matcher(receivedMessage);
        if (matcher.matches()) {
            String date = matcher.group(1);
            String notification = matcher.group(3);
            try {
                LocalDateTime time = LocalDateTime.parse(date, dateTimeFormatter);
                notificationTask.setChatId(update.message().chat().id());
                notificationTask.setNotification(notification);
                notificationTask.setTime(time.truncatedTo(ChronoUnit.MINUTES));
                if (time.isBefore(LocalDateTime.now())) {
                    responseTelegramBot(update, "Are you out of your mind? I can't turn back time");
                } else if (notificationTaskService.existsByNotificationAndChatIdAndTime(notification, update.message().chat().id(), time)) {
                    logger.error("Such notification already exists");
                    responseTelegramBot(update, "Such notification already exists.");
                } else {
                    addNotification(notificationTask);
                    responseTelegramBot(update, "Notify task is in my head now. Anku will remind you to do: \n"
                            + receivedMessage + "\n when the time comes");
                }
            } catch (Exception exception) {
                responseTelegramBot(update, "Received illegal Date/Time format");
            }
        } else {
            responseTelegramBot(update, "Received illegal Date/Time format");
        }
    }

    public void responseTelegramBot(Update update, String response) {
        SendMessage sentMessage = new SendMessage(update.message().chat().id(), response);
        SendResponse sentResponse = telegramBot.execute(sentMessage);
        if (!sentResponse.isOk()) {
            logger.error("An error occured when the method *responseTelegramBot* was invoked.");
        }
    }

    public void addNotification(NotificationTask notificationTask) {
        logger.info("Method *addNotification* was invoked");
        notificationTaskService.saveNotificationTask(notificationTask);
    }
}
