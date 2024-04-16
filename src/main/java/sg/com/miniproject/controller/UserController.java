package sg.com.miniproject.controller;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.StringReader;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sg.com.miniproject.models.User;
import sg.com.miniproject.services.UserService;

@RestController
@RequestMapping(path = "/api/user")
public class UserController {

  @Autowired
  private UserService userService;

  @PostMapping(path = "/register")
  public ResponseEntity<String> register(@RequestBody String req) {
    JsonObject jsonObject = Json
      .createReader(new StringReader(req))
      .readObject();

    String email = jsonObject.getString("email");
    String password = jsonObject.getString("password");
    String name = jsonObject.getString("name");
    Integer mobile = jsonObject.getInt("mobile");

    if (userService.register(email, password, name, mobile)) {
      return ResponseEntity.ok("{\"msg\":\"success\"}");
    }

    return ResponseEntity.ok("{\"msg\":\"failed\"}");
  }

  @PostMapping(path = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> login(@RequestBody String req) {
    JsonObject jsonObject = Json
      .createReader(new StringReader(req))
      .readObject();

    String email = jsonObject.getString("email");
    String password = jsonObject.getString("password");

    String msg = userService.login(email, password);

    switch (msg) {
      case "bad credentials":
        return ResponseEntity.status(401).body("{\"msg\":\"bad credentials\"}");
      default:
        break;
    }
    return ResponseEntity.ok(String.format("{\"msg\":\"%s\"}", msg));
  }

  @PostMapping(path = "/check-token")
  public ResponseEntity<String> checkToken(@RequestBody String req) {
    JsonObject jsonObject = Json
      .createReader(new StringReader(req))
      .readObject();

    String token = jsonObject.getString("token");

    Optional<User> opt = userService.checkToken(token);

    if (opt.isEmpty()) {
      return ResponseEntity.ok("{\"msg\":\"unauthenticated\"}");
    }

    User user = opt.get();

    String fcmToken = userService.getFcmToken(user.getEmail());
    if (fcmToken.equals("NOT_SET")) {
      return ResponseEntity.ok(user.toJsonString());
    }

    return ResponseEntity.ok(user.toJsonString(fcmToken));
  }

  @PostMapping(path = "/update-fcm-token")
  public ResponseEntity<String> updateFcmToken(@RequestBody String req) {
    JsonObject jsonObject = Json
      .createReader(new StringReader(req))
      .readObject();

    String email = jsonObject.getString("email");
    String token = jsonObject.getString("token");

    if (userService.updateFcmToken(email, token)) {
      return ResponseEntity.ok("{\"msg\":\"success\"}");
    }

    return ResponseEntity.ok("{\"msg\":\"fail\"}");
  }
}
