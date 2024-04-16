package sg.com.miniproject.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class Event {

  private String _id;
  private String title;
  private String location;
  private String description;
  private Date start;
  private Date end;
  private Date timestamp;

  public Event() {}

  public Event(
    String _id,
    String title,
    String location,
    String description,
    Date start,
    Date end,
    Date timestamp
  ) {
    this._id = _id;
    this.title = title;
    this.location = location;
    this.description = description;
    this.start = start;
    this.end = end;
    this.timestamp = timestamp;
  }

  public String get_id() {
    return this._id;
  }

  public void set_id(String _id) {
    this._id = _id;
  }

  public String getTitle() {
    return this.title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getLocation() {
    return this.location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Date getStart() {
    return this.start;
  }

  public void setStart(Date start) {
    this.start = start;
  }

  public Date getEnd() {
    return this.end;
  }

  public void setEnd(Date end) {
    this.end = end;
  }

  public Date getTimestamp() {
    return this.timestamp;
  }

  public void setTimestamp(Date timestamp) {
    this.timestamp = timestamp;
  }

  public Event _id(String _id) {
    set_id(_id);
    return this;
  }

  public Event title(String title) {
    setTitle(title);
    return this;
  }

  public Event location(String location) {
    setLocation(location);
    return this;
  }

  public Event description(String description) {
    setDescription(description);
    return this;
  }

  public Event start(Date start) {
    setStart(start);
    return this;
  }

  public Event end(Date end) {
    setEnd(end);
    return this;
  }

  public Event timestamp(Date timestamp) {
    setTimestamp(timestamp);
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof Event)) {
      return false;
    }
    Event event = (Event) o;
    return (
      Objects.equals(_id, event._id) &&
      Objects.equals(title, event.title) &&
      Objects.equals(location, event.location) &&
      Objects.equals(description, event.description) &&
      Objects.equals(start, event.start) &&
      Objects.equals(end, event.end) &&
      Objects.equals(timestamp, event.timestamp)
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(
      _id,
      title,
      location,
      description,
      start,
      end,
      timestamp
    );
  }

  @Override
  public String toString() {
    return (
      "{" +
      " _id='" +
      get_id() +
      "'" +
      ", title='" +
      getTitle() +
      "'" +
      ", location='" +
      getLocation() +
      "'" +
      ", description='" +
      getDescription() +
      "'" +
      ", start='" +
      getStart() +
      "'" +
      ", end='" +
      getEnd() +
      "'" +
      ", timestamp='" +
      getTimestamp() +
      "'" +
      "}"
    );
  }

  public String toStringNoId() {
    SimpleDateFormat dateFormat = new SimpleDateFormat(
      "EEE MMM dd HH:mm:ss zzz yyyy",
      Locale.ENGLISH
    );
    StringBuilder sb = new StringBuilder();
    sb.append(title).append("\n");
    sb.append("Location: ").append(location).append("\n");
    sb.append("Description: ").append(description).append("\n");
    sb.append("Start: ").append(dateFormat.format(start)).append("\n");
    sb.append("End: ").append(dateFormat.format(end)).append("\n");
    return sb.toString();
  }
}
