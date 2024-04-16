package sg.com.miniproject.services;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sg.com.miniproject.models.Event;
import sg.com.miniproject.models.Group;
import sg.com.miniproject.models.GroupEvents;
import sg.com.miniproject.repo.CalendarRepo;

@Service
public class CalendarService {

  @Autowired
  private CalendarRepo calendarRepo;

  @Value("${spring.railway.url}")
  String RAILWAY_URL;

  public boolean createGroup(String email, Group group) {
    String groupName = group.getGroupName();
    Optional<Group> opt = getGroup(groupName);

    if (!opt.isEmpty()) {
      System.out.println("group exists");
      return false;
    }

    try {
      calendarRepo.createGroup(group);

      Document groupDoc = calendarRepo.getGroup(groupName);

      if (groupDoc == null || groupDoc.isEmpty()) {
        return false;
      }

      String group_id = groupDoc.get("_id").toString();
      ObjectId objId = new ObjectId(group_id);

      Document doc = new Document();
      doc.append("group_id", objId);
      doc.append("groupName", groupName);

      calendarRepo.upsertGroupToUser(email, doc);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return false;
    }
    return true;
  }

  public Optional<Group> getGroup(String groupName) {
    Document groupDoc = calendarRepo.getGroup(groupName);

    if (groupDoc == null || groupDoc.isEmpty()) {
      return Optional.empty();
    }

    Group group = new Group(groupDoc);

    return Optional.of(group);
  }

  public String generateInviteLink(String group_id) {
    StringBuilder sb = new StringBuilder();
    sb.append(RAILWAY_URL);
    // sb.append("http://localhost:4200");
    sb.append("/#/join/");
    sb.append(group_id);
    String invite = sb.toString();

    return invite;
  }

  public boolean joinGroup(String email, String group_id) {
    try {
      calendarRepo.joinGroup(email, group_id);

      Document groupDoc = calendarRepo.getGroupById(group_id);
      String groupName = groupDoc.get("groupName").toString();
      ObjectId objId = new ObjectId(group_id);

      Document doc = new Document();
      doc.append("group_id", objId);
      doc.append("groupName", groupName);

      calendarRepo.upsertGroupToUser(email, doc);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return false;
    }
    return true;
  }

  public boolean createEvent(
    String group_id,
    String title,
    String description,
    String location,
    String start,
    String end
  ) {
    ObjectId group_objectId = new ObjectId(group_id);

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
      "yyyy-MM-dd'T'HH:mm:ss.SSSX"
    );
    OffsetDateTime offsetStart = OffsetDateTime.parse(start, formatter);
    OffsetDateTime offsetEnd = OffsetDateTime.parse(end, formatter);

    Date startDate = Date.from(offsetStart.toInstant());
    Date endDate = Date.from(offsetEnd.toInstant());
    Date timestampDate = new Date();

    // need the object_id to pass to mongodb
    ObjectId _id = new ObjectId();
    Document eventDoc = new Document();
    eventDoc.append("_id", _id);
    eventDoc.append("title", title);
    eventDoc.append("location", location);
    eventDoc.append("description", description);
    eventDoc.append("start", startDate);
    eventDoc.append("end", endDate);
    eventDoc.append("timestamp", timestampDate);

    return calendarRepo.createEvent(group_objectId, eventDoc);
  }

  public String editEvent(
    String group_id,
    String _id,
    String title,
    String description,
    String location,
    String start,
    String end,
    String timestamp
  ) {
    String msg = "error";
    ObjectId group_objectId = new ObjectId(group_id);
    ObjectId event_objectId = new ObjectId(_id);

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
      "yyyy-MM-dd'T'HH:mm:ss.SSSX"
    );
    OffsetDateTime offsetStart = OffsetDateTime.parse(start, formatter);
    OffsetDateTime offsetEnd = OffsetDateTime.parse(end, formatter);
    OffsetDateTime offsetTimestamp = OffsetDateTime.parse(timestamp, formatter);

    Date startDate = Date.from(offsetStart.toInstant());
    Date endDate = Date.from(offsetEnd.toInstant());
    Date timestampDate = Date.from(offsetTimestamp.toInstant());

    Document eventDoc = calendarRepo.getEvent(
      group_objectId,
      event_objectId,
      timestampDate
    );

    if (eventDoc == null) {
      msg = "Could not find event in database";
      return msg;
    }

    System.out.println(eventDoc.toJson());

    if (!eventDoc.getDate("timestamp").equals(timestampDate)) {
      msg = "This event has been changed already, please refresh";
      return msg;
    }

    Date newTimestamp = new Date();

    Event event = new Event(
      _id,
      title,
      location,
      description,
      startDate,
      endDate,
      newTimestamp
    );
    if (calendarRepo.editEvent(group_objectId, event_objectId, event)) {
      msg = "Successfully edited event";
    }
    return msg;
  }

  public boolean deleteEvent(String group_id, String _id) {
    ObjectId group_objectId = new ObjectId(group_id);
    ObjectId event_objectId = new ObjectId(_id);

    return calendarRepo.deleteEvent(group_objectId, event_objectId);
  }

  public List<GroupEvents> getUserGroupEvents(String email) {
    return calendarRepo.getUserGroupsAndEvents(email);
  }

  public List<Group> getAllGroups() {
    return calendarRepo.getAllGroups();
  }
}
