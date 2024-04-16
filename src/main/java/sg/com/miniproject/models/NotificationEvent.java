package sg.com.miniproject.models;

import java.util.List;
import java.util.Objects;
import org.bson.types.ObjectId;

public class NotificationEvent {

  private Event event;
  private ObjectId group_id;
  private List<String> members;
  private List<String> fcmTokens;
  private Long telegramToken;

  public NotificationEvent() {}

  public NotificationEvent(
    Event event,
    ObjectId group_id,
    List<String> members,
    List<String> fcmTokens,
    Long telegramToken
  ) {
    this.event = event;
    this.group_id = group_id;
    this.members = members;
    this.fcmTokens = fcmTokens;
    this.telegramToken = telegramToken;
  }

  public Event getEvent() {
    return this.event;
  }

  public void setEvent(Event event) {
    this.event = event;
  }

  public ObjectId getGroup_id() {
    return this.group_id;
  }

  public void setGroup_id(ObjectId group_id) {
    this.group_id = group_id;
  }

  public List<String> getMembers() {
    return this.members;
  }

  public void setMembers(List<String> members) {
    this.members = members;
  }

  public List<String> getFcmTokens() {
    return this.fcmTokens;
  }

  public void setFcmTokens(List<String> fcmTokens) {
    this.fcmTokens = fcmTokens;
  }

  public Long getTelegramToken() {
    return this.telegramToken;
  }

  public void setTelegramToken(Long telegramToken) {
    this.telegramToken = telegramToken;
  }

  public NotificationEvent event(Event event) {
    setEvent(event);
    return this;
  }

  public NotificationEvent group_id(ObjectId group_id) {
    setGroup_id(group_id);
    return this;
  }

  public NotificationEvent members(List<String> members) {
    setMembers(members);
    return this;
  }

  public NotificationEvent fcmTokens(List<String> fcmTokens) {
    setFcmTokens(fcmTokens);
    return this;
  }

  public NotificationEvent telegramToken(Long telegramToken) {
    setTelegramToken(telegramToken);
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof NotificationEvent)) {
      return false;
    }
    NotificationEvent notificationEvent = (NotificationEvent) o;
    return (
      Objects.equals(event, notificationEvent.event) &&
      Objects.equals(group_id, notificationEvent.group_id) &&
      Objects.equals(members, notificationEvent.members) &&
      Objects.equals(fcmTokens, notificationEvent.fcmTokens) &&
      Objects.equals(telegramToken, notificationEvent.telegramToken)
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(event, group_id, members, fcmTokens, telegramToken);
  }

  @Override
  public String toString() {
    return (
      "{" +
      " event='" +
      getEvent() +
      "'" +
      ", group_id='" +
      getGroup_id() +
      "'" +
      ", members='" +
      getMembers() +
      "'" +
      ", fcmTokens='" +
      getFcmTokens() +
      "'" +
      ", telegramToken='" +
      getTelegramToken() +
      "'" +
      "}"
    );
  }
}
