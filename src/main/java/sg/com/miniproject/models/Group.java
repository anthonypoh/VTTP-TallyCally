package sg.com.miniproject.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import org.bson.Document;
import org.bson.types.ObjectId;

public class Group {

  private ObjectId _id;
  private String groupName;
  private List<String> members;
  private List<Event> events;

  public Group() {}

  public Group(String groupName, List<String> members, List<Event> events) {
    this.groupName = groupName;
    this.members = members;
    this.events = events;
  }

  public Group(Document doc) {
    this._id = doc.getObjectId("_id");
    this.groupName = doc.getString("groupName");
    this.members = doc.getList("members", String.class);
    this.events = parseEvents(doc.get("events", List.class));
  }

  // If decide to change to User object
  // private List<User> parseMembers(List<Document> membersDoc) {
  //   List<User> members = new ArrayList<>();
  //   for (Document memberDoc : membersDoc) {
  //     // parse users to user obj
  //   }
  //   return members;
  // }

  private List<Event> parseEvents(List<Document> eventsDoc) {
    List<Event> events = new ArrayList<>();
    for (Document eventDoc : eventsDoc) {
      String _id = eventDoc.get("_id").toString();
      String title = eventDoc.getString("title");
      String description = eventDoc.getString("description");
      String location = eventDoc.getString("location");
      Date start = eventDoc.getDate("start");
      Date end = eventDoc.getDate("end");
      Date timestamp = eventDoc.getDate("timestamp");
      Event event = new Event(
        _id,
        title,
        location,
        description,
        start,
        end,
        timestamp
      );
      events.add(event);
    }
    return events;
  }

  public Group(
    ObjectId _id,
    String groupName,
    List<String> members,
    List<Event> events
  ) {
    this._id = _id;
    this.groupName = groupName;
    this.members = members;
    this.events = events;
  }

  public ObjectId get_id() {
    return this._id;
  }

  public void set_id(ObjectId _id) {
    this._id = _id;
  }

  public String getGroupName() {
    return this.groupName;
  }

  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }

  public List<String> getMembers() {
    return this.members;
  }

  public void setMembers(List<String> members) {
    this.members = members;
  }

  public List<Event> getEvents() {
    return this.events;
  }

  public void setEvents(List<Event> events) {
    this.events = events;
  }

  public Group _id(ObjectId _id) {
    set_id(_id);
    return this;
  }

  public Group groupName(String groupName) {
    setGroupName(groupName);
    return this;
  }

  public Group members(List<String> members) {
    setMembers(members);
    return this;
  }

  public Group events(List<Event> events) {
    setEvents(events);
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof Group)) {
      return false;
    }
    Group group = (Group) o;
    return (
      Objects.equals(_id, group._id) &&
      Objects.equals(groupName, group.groupName) &&
      Objects.equals(members, group.members) &&
      Objects.equals(events, group.events)
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(_id, groupName, members, events);
  }

  @Override
  public String toString() {
    return (
      "{" +
      " _id='" +
      get_id() +
      "'" +
      ", groupName='" +
      getGroupName() +
      "'" +
      ", members='" +
      getMembers() +
      "'" +
      ", events='" +
      getEvents() +
      "'" +
      "}"
    );
  }
}
