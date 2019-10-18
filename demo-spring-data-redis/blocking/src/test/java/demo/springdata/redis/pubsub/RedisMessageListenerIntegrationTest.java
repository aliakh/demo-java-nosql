package demo.springdata.redis.pubsub;

import demo.springdata.redis.repository.RedisConfiguration;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.time.LocalTime;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RedisConfiguration.class)
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class RedisMessageListenerIntegrationTest {

    private static RedisServer redisServer;

    @Autowired
    RedisMessagePublisher redisMessagePublisher;

    @BeforeClass
    public static void beforeClass() throws IOException {
        redisServer = new RedisServer(6379);
        redisServer.start();
    }

    @AfterClass
    public static void afterClass() {
        redisServer.stop();
    }

    @Test
    public void testPublishSubscribe() throws Exception {
        String message = "Message " + LocalTime.now();
        redisMessagePublisher.publish(message);
        Thread.sleep(100);
        assertTrue(RedisMessageSubscriber.getMessages().get(0).contains(message));
    }
}