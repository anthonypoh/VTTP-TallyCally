package sg.com.miniproject.services;

import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sg.com.miniproject.models.User;
import sg.com.miniproject.repo.UserRepo;

@Service
public class UserService {

  @Autowired
  private UserRepo userRepo;

  public boolean register(
    String email,
    String password,
    String name,
    Integer mobile
  ) {
    return userRepo.register(email, password, name, mobile);
  }

  public String login(String email, String password) {
    Optional<User> opt = userRepo.login(email, password);
    if (opt.isEmpty()) {
      return "bad credentials";
    }
    String token = generateUUID();

    if (!userRepo.insertToken(email, token)) {
      return "failed to insert token";
    }
    return token;
  }

  private String generateUUID() {
    return UUID.randomUUID().toString();
  }

  public Optional<User> checkToken(String token) {
    String email = userRepo.checkToken(token);

    if (email == "") {
      return Optional.empty();
    }

    return userRepo.getAuthUser(email);
  }

  public String getFcmToken(String email) {
    return userRepo.getFcmToken(email);
  }

  public boolean internalCheckToken(String email, String token) {
    if (email.equals(userRepo.checkToken(token))) {
      return true;
    }
    return false;
  }

  public boolean updateFcmToken(String email, String token) {
    return userRepo.insertFcmToken(email, token);
  }
}
