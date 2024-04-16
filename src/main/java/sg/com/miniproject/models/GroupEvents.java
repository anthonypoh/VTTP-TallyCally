package sg.com.miniproject.models;

import java.util.List;
import java.util.Objects;

public class GroupEvents {

  private String email;
  private String groupName;
  private List<Event> events;
  private String group_id;

  public GroupEvents() {}

  public GroupEvents(
    String email,
    String groupName,
    List<Event> events,
    String group_id
  ) {
    this.email = email;
    this.groupName = groupName;
    this.events = events;
    this.group_id = group_id;
  }

  public String getEmail() {
    return this.email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getGroupName() {
    return this.groupName;
  }

  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }

  public List<Event> getEvents() {
    return this.events;
  }

  public void setEvents(List<Event> events) {
    this.events = events;
  }

  public String getGroup_id() {
    return this.group_id;
  }

  public void setGroup_id(String group_id) {
    this.group_id = group_id;
  }

  public GroupEvents email(String email) {
    setEmail(email);
    return this;
  }

  public GroupEvents groupName(String groupName) {
    setGroupName(groupName);
    return this;
  }

  public GroupEvents events(List<Event> events) {
    setEvents(events);
    return this;
  }

  public GroupEvents group_id(String group_id) {
    setGroup_id(group_id);
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof GroupEvents)) {
      return false;
    }
    GroupEvents groupEvents = (GroupEvents) o;
    return (
      Objects.equals(email, groupEvents.email) &&
      Objects.equals(groupName, groupEvents.groupName) &&
      Objects.equals(events, groupEvents.events) &&
      Objects.equals(group_id, groupEvents.group_id)
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(email, groupName, events, group_id);
  }

  @Override
  public String toString() {
    return (
      "{" +
      " email='" +
      getEmail() +
      "'" +
      ", groupName='" +
      getGroupName() +
      "'" +
      ", events='" +
      getEvents() +
      "'" +
      ", group_id='" +
      getGroup_id() +
      "'" +
      "}"
    );
  }
}
