package pro.sky.telegrambot.responder;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.listener.TelegramBotUpdatesListener;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.service.NotificationTaskService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;

@Service
public class TelegramBotReminder {
    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final TelegramBot telegramBot;

    private final NotificationTaskService notificationTaskService;

    public TelegramBotReminder(TelegramBot telegramBot, NotificationTaskService notificationTaskService) {
        this.telegramBot = telegramBot;
        this.notificationTaskService = notificationTaskService;
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void reminder() {
        try {
            logger.info("Checking tasks each minute like if I was mad");
            LocalDateTime time = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
            Collection<NotificationTask> notificationTasks = notificationTaskService.findAllByTime(time);
            notificationTasks.forEach(notificationTask -> {
                Long notificationTaskId = notificationTask.getChatId();
                telegramBot.execute(new SendMessage(notificationTaskId, notificationTask.getNotification()));
            });
        } catch (IllegalArgumentException exception) {
            logger.error("An error occured when the method *responseTelegramBot* was invoked.");
        }
    }
}
