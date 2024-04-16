package sg.com.miniproject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import sg.com.miniproject.repo.TelegramRepo;
import sg.com.miniproject.services.TelegramService;

@SpringBootApplication
@EnableScheduling
public class MiniProjectApplication implements CommandLineRunner {

  @Value("${spring.bot.name}")
  private String botUsername;

  @Value("${spring.bot.token}")
  private String botToken;

  @Autowired
  private TelegramService telegramService;

  @Autowired
  private TelegramRepo telegramRepo;

  @Autowired
  private MongoTemplate mongoTemplate;

  public static void main(String[] args) {
    SpringApplication.run(MiniProjectApplication.class, args);
  }

  @Override
  public void run(String... args) throws TelegramApiException {
    TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
    System.out.println(botsApi);
    try {
      botsApi.registerBot(
        new TelegramBot(
          botUsername,
          botToken,
          telegramService,
          telegramRepo,
          mongoTemplate
        )
      );
      System.out.println("Telegram Bot is ready to receive updates!");
    } catch (TelegramApiException e) {
      e.printStackTrace();
    }
  }
}
