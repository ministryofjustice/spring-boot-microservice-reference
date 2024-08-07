package com.cgi.example.petstore.utils.embedded;

import static com.cgi.example.petstore.utils.ProcessManagement.waitUntil;

import com.cgi.example.common.local.DynamicApplicationPropertiesRepository;
import de.flapdoodle.embed.mongo.commands.ServerAddress;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.transitions.ImmutableMongod;
import de.flapdoodle.embed.mongo.transitions.Mongod;
import de.flapdoodle.embed.mongo.transitions.RunningMongodProcess;
import de.flapdoodle.reverse.TransitionWalker;
import de.flapdoodle.reverse.transitions.Start;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.springframework.data.mongodb.core.MongoTemplate;

@Slf4j
@Disabled("Not a test class")
public class MongoDBEmbedded {

  private final DynamicApplicationPropertiesRepository propertiesRepository =
      new DynamicApplicationPropertiesRepository();

  private TransitionWalker.ReachedState<RunningMongodProcess> runningMongoDB;

  public static void main(String[] args) {
    new MongoDBEmbedded();
  }

  public MongoDBEmbedded() {
    if (isRunning()) {
      log.debug("Cannot start MongoDB Embedded as it is already running");
      return;
    }

    ImmutableMongod mongoDB =
        Mongod.builder().net(Start.to(Net.class).initializedWith(Net.defaults())).build();

    log.info("Starting MongoDB Embedded");
    executeAsDetachedThread(() -> runningMongoDB = mongoDB.start(Version.V4_4_18));

    waitUntil(this::isRunning);

    storeMongoDBPortNumber();
    blockAndWait();
  }

  private void storeMongoDBPortNumber() {
    ServerAddress serverAddress = runningMongoDB.current().getServerAddress();
    String host = serverAddress.getHost();
    int port = serverAddress.getPort();
    log.info("Started MongoDB Embedded on {}:{}", host, port);

    propertiesRepository.setMongoDBPort(getClass(), port);
  }

  private void blockAndWait() {
    try {
      System.in.read();
    } catch (IOException e) {
      log.info("MongoDB Embedded process is exiting: [{}]", e.getMessage(), e);
    }
  }

  private void executeAsDetachedThread(Runnable runnable) {
    Thread detachedThread = new Thread(runnable);
    detachedThread.start();
  }

  private boolean isRunning() {
    return Objects.nonNull(runningMongoDB)
        && Objects.nonNull(runningMongoDB.current())
        && runningMongoDB.current().isAlive();
  }

  public void resetAllUsing(MongoTemplate mongoTemplate) {
    Set<String> collectionNames = mongoTemplate.getCollectionNames();

    collectionNames.forEach(mongoTemplate::dropCollection);
  }
}
