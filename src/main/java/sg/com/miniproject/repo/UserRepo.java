package sg.com.miniproject.repo;

import java.sql.Timestamp;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import sg.com.miniproject.models.User;

@Repository
public class UserRepo {

  @Autowired
  private JdbcTemplate template;

  @Autowired
  private MongoTemplate mongoTemplate;

  public static final String SQL_INSERT_USER =
    "INSERT INTO users VALUES (?,?,?,?)";

  public static final String SQL_INSERT_USER_COOKIE =
    "INSERT INTO user_cookies (email, token) VALUES (?,?)";

  public static final String SQL_GET_USER =
    "SELECT * FROM users where email = ? and password = ?";

  public static final String SQL_GET_AUTH_USER =
    "SELECT * FROM users where email = ?";

  public static final String SQL_GET_USER_COOKIE =
    "SELECT * FROM user_cookies where token = ?";

  public static final String SQL_INSERT_FCM_TOKEN =
    "INSERT INTO fcm_token (email, token, timestamp) VALUES (?,?,?) ON DUPLICATE KEY UPDATE email = VALUES(email), token = VALUES(token), timestamp = VALUES(timestamp)";

  public static final String SQL_GET_FCM_TOKEN =
    "SELECT token FROM fcm_token WHERE email = ?";

  public Boolean register(
    String email,
    String password,
    String name,
    Integer mobile
  ) {
    try {
      return (
        template.update(SQL_INSERT_USER, email, password, name, mobile) >= 1
      );
    } catch (DataAccessException e) {
      System.out.println(e.getMessage());
    }

    return false;
  }

  public Optional<User> login(String email, String password) {
    SqlRowSet rs = template.queryForRowSet(SQL_GET_USER, email, password);
    if (!rs.next()) {
      return Optional.empty();
    }

    User user = new User();
    user.setMobile(rs.getInt("mobile"));
    user.setName(rs.getString("name"));
    user.setEmail(rs.getString("email"));

    return Optional.of(user);
  }

  public Optional<User> getAuthUser(String email) {
    SqlRowSet rs = template.queryForRowSet(SQL_GET_AUTH_USER, email);
    if (!rs.next()) {
      return Optional.empty();
    }

    User user = new User();
    user.setMobile(rs.getInt("mobile"));
    user.setName(rs.getString("name"));
    user.setEmail(rs.getString("email"));

    return Optional.of(user);
  }

  public boolean insertToken(String email, String token) {
    try {
      return (template.update(SQL_INSERT_USER_COOKIE, email, token) >= 1);
    } catch (DataAccessException e) {
      System.out.println(e.getMessage());
    }
    return false;
  }

  public String checkToken(String token) {
    SqlRowSet rs = template.queryForRowSet(SQL_GET_USER_COOKIE, token);
    while (rs.next()) {
      if (rs.getString("token").equals(token)) {
        return rs.getString("email");
      }
    }
    return "";
  }

  public boolean insertFcmToken(String email, String token) {
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    try {
      return (
        template.update(SQL_INSERT_FCM_TOKEN, email, token, timestamp) >= 1
      );
    } catch (DataAccessException e) {
      System.out.println(e.getMessage());
    }
    return false;
  }

  public String getFcmToken(String email) {
    SqlRowSet rs = template.queryForRowSet(SQL_GET_FCM_TOKEN, email);
    if (!rs.next()) {
      return "NOT_SET";
    }

    return rs.getString("token");
  }
}
