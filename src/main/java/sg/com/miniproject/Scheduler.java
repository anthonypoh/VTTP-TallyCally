package sg.com.miniproject;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sg.com.miniproject.models.Event;
import sg.com.miniproject.models.Group;
import sg.com.miniproject.models.NotificationEvent;
import sg.com.miniproject.services.CalendarService;
import sg.com.miniproject.services.NotificationService;
import sg.com.miniproject.services.TelegramService;
import sg.com.miniproject.services.UserService;

@Component
public class Scheduler {

  @Autowired
  private CalendarService calendarService;

  @Autowired
  private UserService userService;

  @Autowired
  private NotificationService notificationService;

  @Autowired
  private TelegramBot telegramBot;

  @Autowired
  private TelegramService telegramService;

  private List<Group> groups;
  private List<NotificationEvent> notificationEvents;

  @Scheduled(fixedRate = 60000)
  public void checkSchedule() {
    List<Group> groups = calendarService.getAllGroups();

    if (groups.equals(this.groups)) {
      return;
    }
    this.groups = groups;

    this.notificationEvents = new ArrayList<>();

    for (Group group : groups) {
      for (Event event : group.getEvents()) {
        List<String> fcmTokens = new ArrayList<>();
        for (String member : group.getMembers()) {
          String fcmToken = userService.getFcmToken(member);
          if (!fcmToken.equals("NOT_SET")) {
            fcmTokens.add(fcmToken);
          }
        }

        NotificationEvent nEvent = new NotificationEvent();
        nEvent.setEvent(event);
        nEvent.setGroup_id(group.get_id());
        nEvent.setMembers(group.getMembers());
        nEvent.setFcmTokens(fcmTokens);
        nEvent.setTelegramToken(
          telegramService.getChatIdByGroupId(group.get_id())
        );
        this.notificationEvents.add(nEvent);
      }
    }
  }

  @Scheduled(fixedRate = 60000)
  public void executeNotificationEvents() {
    if (this.notificationEvents.size() <= 0) {
      return;
    }

    LocalDateTime now = LocalDateTime.now();

    for (NotificationEvent nEvent : this.notificationEvents) {
      Date eventStartDate = nEvent.getEvent().getStart();
      LocalDateTime eventStartTime = LocalDateTime.ofInstant(
        eventStartDate.toInstant(),
        ZoneId.systemDefault()
      );
      Duration timeUntilEventStarts = Duration.between(now, eventStartTime);
      long hoursUntilEventStarts = timeUntilEventStarts.toHours();

      if (hoursUntilEventStarts == 1) {
        String title = "Your event is in 1 hour.";
        String body = nEvent.getEvent().toStringNoId();

        for (String fcmToken : nEvent.getFcmTokens()) {
          notificationService.sendNotification(fcmToken, title, body);
        }
        telegramBot.sendNotification(nEvent.getTelegramToken(), body);
      }
    }
  }
}
