package sg.com.miniproject.services;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

  public void sendNotification(String token, String title, String body) {
    Message message = Message
      .builder()
      .putData("title", title)
      .putData("body", body)
      .setToken(token)
      .build();

    System.out.println(message);

    try {
      FirebaseMessaging.getInstance().send(message);
    } catch (FirebaseMessagingException e) {
      e.printStackTrace();
    }
  }

  public void test() {
    System.out.println("testing");
  }
}
