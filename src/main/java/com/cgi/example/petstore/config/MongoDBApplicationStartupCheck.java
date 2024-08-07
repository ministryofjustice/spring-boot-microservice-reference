package com.cgi.example.petstore.config;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MongoDBApplicationStartupCheck implements ApplicationListener<ApplicationReadyEvent> {

  private static final String MONGO_DB_STATUS_KEY = "ok";
  private static final String SERVER_STATUS_COMMAND = "{ serverStatus: 1 }";

  private final MongoTemplate mongoTemplate;

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    Document mongoStatus = mongoTemplate.executeCommand(SERVER_STATUS_COMMAND);

    if (mongoDBAvailable(mongoStatus)) {
      log.debug("Successfully verified connectivity to MongoDB");
    } else {
      throw new IllegalStateException("Unable to verify connectivity to MongoDB");
    }
  }

  private boolean mongoDBAvailable(Document mongoStatus) {
    return mongoStatus.containsKey(MONGO_DB_STATUS_KEY)
        && Objects.nonNull(mongoStatus.get(MONGO_DB_STATUS_KEY));
  }
}
