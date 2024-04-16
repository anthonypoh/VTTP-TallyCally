package sg.com.miniproject.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sg.com.miniproject.models.Event;
import sg.com.miniproject.models.Group;
import sg.com.miniproject.models.GroupEvents;
import sg.com.miniproject.services.CalendarService;
import sg.com.miniproject.services.UserService;

@RestController
@RequestMapping(path = "/api/calendar")
public class CalendarController {

  @Autowired
  private CalendarService calendarService;

  @Autowired
  private UserService userService;

  // GROUP MAPPINGS

  @GetMapping(path = "/get-group")
  public ResponseEntity<String> getGroup(@RequestParam String groupName) {
    calendarService.getGroup(groupName);
    Optional<Group> opt = calendarService.getGroup(groupName);

    if (opt.isEmpty()) {
      return ResponseEntity.badRequest().body("{\"msg\":\"group not found.\"}");
    }

    return ResponseEntity.ok(opt.get().toString());
  }

  @GetMapping(path = "/generate-invite-link")
  public ResponseEntity<String> generateInviteLink(
    @RequestParam String group_id
  ) {
    String invite = calendarService.generateInviteLink(group_id);

    return ResponseEntity.ok(String.format("{\"msg\":\"%s\"}", invite));
  }

  @GetMapping(path = "/get-user-groups")
  public ResponseEntity<String> getUserGroups(@RequestParam String email) {
    return ResponseEntity.ok("");
  }

  @PostMapping(path = "/create-group")
  public ResponseEntity<String> createGroup(@RequestBody String req) {
    JsonObject jsonObject = Json
      .createReader(new StringReader(req))
      .readObject();

    String groupName = jsonObject.getString("groupName");
    String email = jsonObject.getString("email");

    List<String> members = new ArrayList<>();
    members.add(email);

    List<Event> events = new ArrayList<>();

    Group group = new Group(groupName, members, events);

    if (calendarService.createGroup(email, group)) {
      return ResponseEntity.ok("{\"msg\":\"success\"}");
    }

    return ResponseEntity.internalServerError().body("{\"msg\":\"fail\"}");
  }

  @PostMapping(path = "/join-group/{group_id}")
  public ResponseEntity<String> joinGroup(
    @PathVariable String group_id,
    @RequestBody String req
  ) {
    JsonObject jsonObject = Json
      .createReader(new StringReader(req))
      .readObject();

    String email = jsonObject.getString("email");

    if (calendarService.joinGroup(email, group_id)) {
      return ResponseEntity.ok("{\"msg\":\"success\"}");
    }
    return ResponseEntity.internalServerError().body("{\"msg\":\"fail\"}");
  }

  // EVENT MAPPINGS

  @PutMapping(path = "/create-event/{group_id}")
  public ResponseEntity<String> createEvent(
    @RequestBody String req,
    @PathVariable String group_id
  ) {
    JsonObject jsonObject = Json
      .createReader(new StringReader(req))
      .readObject();

    String title = jsonObject.getString("title");
    String description = jsonObject.getString("description");
    String location = jsonObject.getString("location");
    String start = jsonObject.getString("start");
    String end = jsonObject.getString("end");

    if (
      calendarService.createEvent(
        group_id,
        title,
        description,
        location,
        start,
        end
      )
    ) {
      return ResponseEntity.ok("{\"msg\":\"success\"}");
    }
    return ResponseEntity.internalServerError().body("{\"msg\":\"fail\"}");
  }

  @PutMapping(path = "/edit-event/{group_id}")
  public ResponseEntity<String> editEvent(
    @PathVariable String group_id,
    @RequestBody String req
  ) {
    JsonObject jsonObject = Json
      .createReader(new StringReader(req))
      .readObject();
    String _id = jsonObject.getString("_id");
    String title = jsonObject.getString("title");
    String description = jsonObject.getString("description");
    String location = jsonObject.getString("location");
    String start = jsonObject.getString("start");
    String end = jsonObject.getString("end");
    String timestamp = jsonObject.getString("timestamp");

    String msg = calendarService.editEvent(
      group_id,
      _id,
      title,
      description,
      location,
      start,
      end,
      timestamp
    );

    if (msg.equals("error")) {
      return ResponseEntity.internalServerError().body("{\"msg\":\"error\"}");
    }

    return ResponseEntity.ok(String.format("{\"msg\":\"%s\"}", msg));
  }

  @DeleteMapping(path = "/delete-event/{group_id}/{_id}")
  public ResponseEntity<String> deleteEvent(
    @PathVariable String group_id,
    @PathVariable String _id
  ) {
    if (calendarService.deleteEvent(group_id, _id)) {
      return ResponseEntity.ok("{\"msg\":\"success\"}");
    }
    return ResponseEntity.internalServerError().body("{\"msg\":\"fail\"}");
  }

  @PostMapping(path = "/get-group-events")
  public ResponseEntity<String> getGroupEvents(@RequestBody String req) {
    JsonObject jsonObject = Json
      .createReader(new StringReader(req))
      .readObject();

    String email = jsonObject.getString("email");
    String token = jsonObject.getString("token");

    if (!userService.internalCheckToken(email, token)) {
      return ResponseEntity.status(401).body("{\"msg\":\"bad credentials\"}");
    }

    List<GroupEvents> groupEvents = calendarService.getUserGroupEvents(email);

    if (groupEvents.size() <= 0) {
      return ResponseEntity.ok("{\"msg\":\"no group found\"}");
    }

    ObjectMapper objectMapper = new ObjectMapper();
    String json;
    try {
      json = objectMapper.writeValueAsString(groupEvents);
      return ResponseEntity.ok(json);
    } catch (JsonProcessingException e) {
      System.out.println(e.getMessage());
    }
    return ResponseEntity
      .internalServerError()
      .body("{\"msg\":\"something went wrong\"}");
  }
}
