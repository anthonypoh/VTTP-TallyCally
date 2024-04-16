package sg.com.miniproject.repo;

import com.mongodb.client.result.UpdateResult;
import java.util.Optional;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
public class TelegramRepo {

  private final MongoTemplate mongoTemplate;

  public TelegramRepo(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  public boolean register(Long chatId, ObjectId _id) {
    Query query = Query.query(Criteria.where("chatId").is(chatId));
    Update updateOps = new Update().set("group_id", _id);
    UpdateResult updateResult = mongoTemplate.upsert(
      query,
      updateOps,
      Document.class,
      "bots"
    );
    if (
      updateResult.getModifiedCount() >= 1 ||
      updateResult.getUpsertedId() != null
    ) {
      return true;
    }
    return false;
  }

  public Document getBot(Long chatId) {
    Query query = Query.query(Criteria.where("chatId").is(chatId));
    return mongoTemplate.findOne(query, Document.class, "bots");
  }

  public ObjectId get_id(Long chatId) {
    Query query = Query.query(Criteria.where("chatId").is(chatId));
    Document doc = mongoTemplate.findOne(query, Document.class, "bots");

    return doc.getObjectId("group_id");
  }

  public Long getChatIdByGroupId(ObjectId group_id) {
    Query query = Query.query(Criteria.where("group_id").is(group_id));
    Document doc = mongoTemplate.findOne(query, Document.class, "bots");
    if (doc != null) {
      return doc.getLong("chatId");
    }
    return null;
  }

  public Optional<AggregationResults<Document>> getEvents(Long chatId) {
    ObjectId group_id = get_id(chatId);

    if (group_id == null) {
      return Optional.empty();
    }

    // Aggregation pipeline to sort events by start date
    Aggregation aggregation = Aggregation.newAggregation(
      Aggregation.match(Criteria.where("_id").is(group_id)),
      Aggregation.unwind("events"),
      Aggregation.sort(Sort.Direction.ASC, "events.start"),
      Aggregation.group("_id").push("events").as("events"),
      Aggregation.project("_id").and("events").as("events"),
      Aggregation.unwind("events"),
      Aggregation.replaceRoot("events")
    );

    // Execute the aggregation pipeline
    AggregationResults<Document> results = mongoTemplate.aggregate(
      aggregation,
      "calendar",
      Document.class
    );

    return Optional.of(results);
  }
}
