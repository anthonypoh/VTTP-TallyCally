package sg.com.miniproject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sg.com.miniproject.services.NotificationService;

@RestController
public class NotificationController {

  @Autowired
  private NotificationService notificationService;

  @PostMapping(path = "/api/send-notification")
  public void sendNotification(
    @RequestParam String token,
    @RequestParam String title,
    @RequestParam String body
  ) {
    notificationService.sendNotification(token, title, body);
  }
}
