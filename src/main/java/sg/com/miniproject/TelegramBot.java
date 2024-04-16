package sg.com.miniproject;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import sg.com.miniproject.models.Event;
import sg.com.miniproject.repo.TelegramRepo;
import sg.com.miniproject.services.TelegramService;

@Component
@Scope("singleton")
public class TelegramBot extends TelegramLongPollingBot {

  private final String botUsername;
  private final String botToken;
  private final TelegramService telegramService;
  private final TelegramRepo telegramRepo;
  private final MongoTemplate mongoTemplate;
  private boolean isRegistered = false;

  public TelegramBot(
    @Value("${spring.bot.name}") String botUsername,
    @Value("${spring.bot.token}") String botToken,
    TelegramService telegramService,
    TelegramRepo telegramRepo,
    MongoTemplate mongoTemplate
  ) {
    super(botToken);
    this.botUsername = botUsername;
    this.botToken = botToken;
    this.mongoTemplate = mongoTemplate;
    this.telegramRepo = new TelegramRepo(this.mongoTemplate);
    this.telegramService = new TelegramService(this.telegramRepo);
  }

  @Override
  public void onUpdateReceived(Update update) {
    if (update.hasMessage() && update.getMessage().hasText()) {
      String messageText = update.getMessage().getText();
      String cmd = messageText.split(" ")[0];
      Long chatId = update.getMessage().getChatId();
      this.isRegistered = isRegistered(chatId);
      System.out.println(chatId);
      
      switch (cmd) {
        case "/start":
          sendWelcomeMessage(chatId);
          break;
        case "/help":
          sendHelpMessage(chatId);
          break;
        case "/register":
          register(messageText, chatId);
          break;
        case "/events":
          sendEventsMessage(chatId);
          break;
        default:
          SendMessage message = new SendMessage();
          message.setChatId(chatId);
          message.setText("You said: " + messageText);
          exec(message);
      }
    }
  }

  private boolean isRegistered(Long chatId) {
    return telegramService.isRegistered(chatId);
  }

  private void sendWelcomeMessage(Long chatId) {
    SendMessage message = new SendMessage();
    message.setChatId(chatId);
    message.setText(
      """
      Hi, please register me with your invite link:\n
      /register insert-link-here\n
      /help for more commands
        """
    );
    exec(message);
  }

  private void sendHelpMessage(Long chatId) {
    SendMessage message = new SendMessage();
    message.setChatId(chatId);
    message.setText(
      """
      /help - List of commands\n
      /register - Register bot with existing group: /register insert-link-here\n
      /events - List of events
        """
    );
    exec(message);
  }

  private void register(String messageText, Long chatId) {
    if (telegramService.register(messageText, chatId)) {
      String msg =
        """
        I've succesfully registered to your group.\n
        By default I will display event notifcations 1 hour before the start time.
          """;
      sendSuccess(chatId, msg);
    } else {
      sendFail(chatId, "internal error");
    }
  }

  private void sendEventsMessage(Long chatId) {
    if (!this.isRegistered) {
      sendFail(
        chatId,
        "This bot hasn't been registed. Please register with the following command:\n/register insert-link-here"
      );
      return;
    }
    List<Event> events = telegramService.getEvents(chatId);
    System.out.println(events.toString());
    StringBuilder sb = new StringBuilder();
    for (Event event : events) {
      sb.append(event.toStringNoId());
      sb.append("\n");
    }
    String msg = sb.toString();

    SendMessage message = new SendMessage();
    message.setChatId(chatId);
    message.setText(msg);
    exec(message);
  }

  private void sendSuccess(Long chatId, String msg) {
    SendMessage message = new SendMessage();
    message.setChatId(chatId);
    message.setText(msg);
    exec(message);
  }

  private void sendFail(Long chatId, String msg) {
    SendMessage message = new SendMessage();
    message.setChatId(chatId);
    message.setText(msg);
    exec(message);
  }

  private void exec(SendMessage message) {
    try {
      execute(message);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void sendNotification(Long chatId, String msg) {
    SendMessage message = new SendMessage();
    message.setChatId(chatId);
    message.setText(msg);
    exec(message);
  }

  @Override
  public String getBotUsername() {
    return botUsername;
  }

  @Override
  public String getBotToken() {
    return botToken;
  }
}
