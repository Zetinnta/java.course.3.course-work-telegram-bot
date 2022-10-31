package pro.sky.telegrambot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class NotificationTaskService {

    private final Logger logger = LoggerFactory.getLogger(NotificationTaskService.class);

    private final NotificationTaskRepository notificationTaskRepository;

    public NotificationTaskService(NotificationTaskRepository notificationTaskRepository) {
        this.notificationTaskRepository = notificationTaskRepository;
    }


    public boolean existsByNotificationAndChatIdAndTime(String notification, Long chatId, LocalDateTime time) {
        return notificationTaskRepository.existsByNotificationAndChatIdAndTime(notification, chatId, time);
    }

    public List<NotificationTask> findAllByTime(LocalDateTime time) {
        return notificationTaskRepository.findAllByTime(time);
    }

    public void saveNotificationTask(NotificationTask notificationTask) {
        notificationTaskRepository.save(notificationTask);
    }
}
