package sg.com.miniproject.models;

import jakarta.json.Json;
import java.util.Objects;

public class User {

  private Integer mobile;
  private String name;
  private String email;
  private String password;
  private String fcmToken;

  public User() {}

  public User(
    Integer mobile,
    String name,
    String email,
    String password,
    String fcmToken
  ) {
    this.mobile = mobile;
    this.name = name;
    this.email = email;
    this.password = password;
    this.fcmToken = fcmToken;
  }

  public User(Integer mobile, String name, String email, String password) {
    this.mobile = mobile;
    this.name = name;
    this.email = email;
    this.password = password;
  }

  public User(Integer mobile, String name, String email) {
    this.mobile = mobile;
    this.name = name;
    this.email = email;
  }

  public Integer getMobile() {
    return this.mobile;
  }

  public void setMobile(Integer mobile) {
    this.mobile = mobile;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return this.email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return this.password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getFcmToken() {
    return this.fcmToken;
  }

  public void setFcmToken(String fcmToken) {
    this.fcmToken = fcmToken;
  }

  public User mobile(Integer mobile) {
    setMobile(mobile);
    return this;
  }

  public User name(String name) {
    setName(name);
    return this;
  }

  public User email(String email) {
    setEmail(email);
    return this;
  }

  public User password(String password) {
    setPassword(password);
    return this;
  }

  public User fcmToken(String fcmToken) {
    setFcmToken(fcmToken);
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof User)) {
      return false;
    }
    User user = (User) o;
    return (
      Objects.equals(mobile, user.mobile) &&
      Objects.equals(name, user.name) &&
      Objects.equals(email, user.email) &&
      Objects.equals(password, user.password) &&
      Objects.equals(fcmToken, user.fcmToken)
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(mobile, name, email, password, fcmToken);
  }

  @Override
  public String toString() {
    return (
      "{" +
      " mobile='" +
      getMobile() +
      "'" +
      ", name='" +
      getName() +
      "'" +
      ", email='" +
      getEmail() +
      "'" +
      ", password='" +
      getPassword() +
      "'" +
      ", fcmToken='" +
      getFcmToken() +
      "'" +
      "}"
    );
  }

  public String toJsonString() {
    return Json
      .createObjectBuilder()
      .add("mobile", this.mobile)
      .add("name", this.name)
      .add("email", this.email)
      // .add("fcmToken", this.fcmToken)
      .build()
      .toString();
  }

  public String toJsonString(String fcmToken) {
    return Json
      .createObjectBuilder()
      .add("mobile", this.mobile)
      .add("name", this.name)
      .add("email", this.email)
      .add("fcmToken", fcmToken)
      .build()
      .toString();
  }
}
