package sg.com.miniproject.repo;

import com.mongodb.client.result.UpdateResult;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import sg.com.miniproject.models.Event;
import sg.com.miniproject.models.Group;
import sg.com.miniproject.models.GroupEvents;

@Repository
public class CalendarRepo {

  @Autowired
  private MongoTemplate mongoTemplate;

  public boolean createGroup(Group group) {
    try {
      mongoTemplate.insert(group, "calendar");
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return false;
    }
    return true;
  }

  public boolean upsertGroupToUser(String email, Document doc) {
    Query query = Query.query(Criteria.where("email").is(email));
    Update updateOps = new Update().push("groups", doc);
    UpdateResult updateResult = mongoTemplate.upsert(
      query,
      updateOps,
      Document.class,
      "users"
    );
    if (
      updateResult.getModifiedCount() >= 1 ||
      updateResult.getUpsertedId() != null
    ) {
      return true;
    }
    return false;
  }

  public Document getGroup(String groupName) {
    Criteria criteria = Criteria.where("groupName").is(groupName);
    Query query = Query.query(criteria);
    Document groupDoc = mongoTemplate.findOne(
      query,
      Document.class,
      "calendar"
    );

    return groupDoc;
  }

  public Document getGroupById(String group_id) {
    Criteria criteria = Criteria.where("_id").is(group_id);
    Query query = Query.query(criteria);
    Document groupDoc = mongoTemplate.findOne(
      query,
      Document.class,
      "calendar"
    );

    return groupDoc;
  }

  public boolean joinGroup(String email, String group_id) {
    Query query = Query.query(Criteria.where("_id").is(group_id));
    Update updateOps = new Update().push("members", email);
    UpdateResult updateResult = mongoTemplate.updateFirst(
      query,
      updateOps,
      Document.class,
      "calendar"
    );
    if (updateResult.getModifiedCount() >= 1) {
      return true;
    }
    return false;
  }

  public boolean createEvent(ObjectId group_ObjectId, Document eventDoc) {
    Query query = Query.query(Criteria.where("_id").is(group_ObjectId));
    Update updateOps = new Update().push("events", eventDoc);
    UpdateResult updateResult = mongoTemplate.updateFirst(
      query,
      updateOps,
      Document.class,
      "calendar"
    );
    if (updateResult.getModifiedCount() >= 1) {
      return true;
    }
    return false;
  }

  public boolean editEvent(
    ObjectId group_ObjectId,
    ObjectId event_objectId,
    Event event
  ) {
    Query query = Query.query(
      Criteria
        .where("_id")
        .is(group_ObjectId)
        .and("events._id")
        .is(event_objectId)
    );

    Update updateOps = new Update().set("events.$", event);
    UpdateResult updateResult = mongoTemplate.updateFirst(
      query,
      updateOps,
      Document.class,
      "calendar"
    );

    System.out.println(updateResult);
    if (updateResult.getModifiedCount() >= 1) {
      return true;
    }
    return false;
  }

  public Document getEvent(
    ObjectId group_id,
    ObjectId event_id,
    Date timestamp
  ) {
    Aggregation aggregation = Aggregation.newAggregation(
      Aggregation.match(Criteria.where("_id").is(group_id)),
      Aggregation.unwind("events"),
      Aggregation.match(Criteria.where("events._id").is(event_id)),
      Aggregation
        .project()
        .and("events._id")
        .as("_id")
        .and("events.title")
        .as("title")
        .and("events.description")
        .as("description")
        .and("events.start")
        .as("start")
        .and("events.end")
        .as("end")
        .and("events.timestamp")
        .as("timestamp")
    );

    List<Document> eventsDoc = mongoTemplate
      .aggregate(aggregation, "calendar", Document.class)
      .getMappedResults();

    return eventsDoc.isEmpty() ? null : eventsDoc.get(0);
  }

  public boolean deleteEvent(ObjectId group_id, ObjectId _id) {
    Criteria criteria = Criteria
      .where("_id")
      .is(group_id)
      .and("events._id")
      .is(_id);
    Query query = Query.query(criteria);
    Update update = new Update()
      .pull("events", Query.query(Criteria.where("_id").is(_id)));

    try {
      mongoTemplate.updateFirst(query, update, "calendar");
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return false;
    }
    return true;
  }

  public List<GroupEvents> getUserGroupsAndEvents(String email) {
    Aggregation aggregation = Aggregation.newAggregation(
      Aggregation.match(Criteria.where("email").is(email)),
      Aggregation.unwind("groups"),
      Aggregation.lookup("calendar", "groups.group_id", "_id", "group"),
      Aggregation.unwind("group"),
      Aggregation
        .group("group._id")
        .first("email")
        .as("email")
        .first("group.groupName")
        .as("groupName")
        .first("group.events")
        .as("events"),
      Aggregation
        .project()
        // .andExclude("_id")
        .andExpression("_id")
        .as("group_id")
        .and("email")
        .as("email")
        .and("groupName")
        .as("groupName")
        .and("events")
        .as("events")
    );

    List<Document> docList = mongoTemplate
      .aggregate(aggregation, "users", Document.class)
      .getMappedResults();

    List<GroupEvents> groupEvents = new ArrayList<>();
    for (Document doc : docList) {
      GroupEvents groupEvent = new GroupEvents();
      groupEvent.setEmail(doc.getString("email"));
      groupEvent.setGroupName(doc.getString("groupName"));
      List<Event> events = parseEvents(doc.getList("events", Document.class));
      groupEvent.setEvents(events);
      groupEvent.setGroup_id(doc.get("group_id", ObjectId.class).toString());
      groupEvents.add(groupEvent);
    }

    // Sort by group name
    Collections.sort(
      groupEvents,
      Comparator.comparing(GroupEvents::getGroupName)
    );

    // Sort events within each group by start date
    SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
    for (GroupEvents eventData : groupEvents) {
      List<Event> events = eventData.getEvents();
      events.sort((e1, e2) -> {
        try {
          Date startDate1 = sdf.parse(e1.getStart().toString());
          Date startDate2 = sdf.parse(e2.getStart().toString());
          return startDate1.compareTo(startDate2);
        } catch (ParseException e) {
          e.printStackTrace();
        }
        return 0;
      });
    }
    return groupEvents;
  }

  public List<Group> getAllGroups() {
    return mongoTemplate.findAll(Group.class, "calendar");
  }

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
}
