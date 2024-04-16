package sg.com.miniproject.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Service;
import sg.com.miniproject.models.Event;
import sg.com.miniproject.repo.TelegramRepo;

@Service
public class TelegramService {

  private final TelegramRepo telegramRepo;

  public TelegramService(TelegramRepo telegramRepo) {
    this.telegramRepo = telegramRepo;
  }

  public boolean register(String messageText, Long chatId) {
    String[] split = messageText.split("/");
    String group_id = split[split.length - 1];
    ObjectId _id = new ObjectId(group_id);

    return telegramRepo.register(chatId, _id);
  }

  public boolean isRegistered(Long chatid) {
    Document doc = telegramRepo.getBot(chatid);
    if (doc != null) {
      return true;
    }
    return false;
  }

  public Long getChatIdByGroupId(ObjectId group_id) {
    return telegramRepo.getChatIdByGroupId(group_id);
  }

  public List<Event> getEvents(Long chatId) {
    List<Event> events = new ArrayList<>();
    Optional<AggregationResults<Document>> opt = telegramRepo.getEvents(chatId);

    if (opt.isEmpty()) {
      return events;
    }

    AggregationResults<Document> results = opt.get();

    for (Document doc : results) {
      Event event = new Event();
      event.set_id(doc.getObjectId("_id").toString());
      event.setTitle(doc.getString("title"));
      event.setDescription(doc.getString("description"));
      event.setLocation(doc.getString("location"));
      event.setStart(doc.getDate("start"));
      event.setEnd(doc.getDate("end"));
      events.add(event);
    }

    return events;
  }
}
