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

import pro.sky.telegrambot.exception.ResponseNotFoundException;
import pro.sky.telegrambot.model.Interaction;
import pro.sky.telegrambot.service.InteractionService;
import pro.sky.telegrambot.service.NotificationTaskService;

import javax.annotation.PostConstruct;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;


@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final InteractionService interactionService;

    private final NotificationTaskService notificationTaskService;

    @Autowired
    private TelegramBot telegramBot;

    public TelegramBotUpdatesListener(InteractionService interactionService, NotificationTaskService notificationTaskService, TelegramBot telegramBot) {
        this.interactionService = interactionService;
        this.notificationTaskService = notificationTaskService;
        this.telegramBot = telegramBot;
    }

    private Collection<Interaction> allInteractions;
    private String message;

    static Locale current = Locale.getDefault();
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.uuuu HH:mm", current);
    private static final Pattern pattern = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
        allInteractions = interactionService.getAllPossibleInteractions();
        boolean expected = false;
        for (Interaction interaction : allInteractions) {
            if (interaction.getRequest().equals("/help")) {
                message = interaction.getResponse();
                expected = true;
            }
        }
        if (!expected) {
            logger.error("An error has occured, check /help handling.");
            throw new ResponseNotFoundException("An error has occured, check /help handling.");
        }
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            //update.message().text();
            //update.message().chat().id();
            String receivedMessage = update.message().text();
            if (receivedMessage.startsWith("/")) {
                boolean expected = false;
                for (Interaction interaction : allInteractions) {
                    if (receivedMessage.equalsIgnoreCase(interaction.getRequest())) {
                        expected = true;
                        responseTelegramBot(update, interaction.getResponse());
                    }
                }
                if (!expected) {
                    responseTelegramBot(update, "Illegal command. Use /help for more information.");
                }
            }
            // Process your updates here
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    public void responseTelegramBot(Update update, String response) {
        SendMessage sentMessage = new SendMessage(update.message().chat().id(), response);
        SendResponse sentResponse = telegramBot.execute(sentMessage);
        if (!sentResponse.isOk()) {
            logger.error("An error occured when the method *responseTelegramBot* was invoked.");
        }
    }

}
