package demo.springdata.redis;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ReactiveListOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import redis.embedded.RedisServer;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SpringDataRedisReactiveApplication.class)
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class ReactiveListOperationsIntegrationTest {

    private static RedisServer redisServer;

    @Autowired
    private ReactiveRedisTemplate<String, String> redisTemplate;

    private ReactiveListOperations<String, String> reactiveListOperations;

    @BeforeClass
    public static void beforeClass() throws IOException {
        redisServer = new RedisServer(6379);
        redisServer.start();
    }

    @AfterClass
    public static void afterClass() {
        redisServer.stop();
    }

    @Before
    public void setup() {
        reactiveListOperations = redisTemplate.opsForList();
    }

    @Test
    public void testList() {
        Mono<Long> leftPush = reactiveListOperations.leftPushAll("List", "Alpha", "Beta")
                .log("leftPush");

        StepVerifier.create(leftPush)
                .expectNext(2L)
                .verifyComplete();

        Mono<String> leftPop = reactiveListOperations.leftPop("List")
                .log("leftPop");

        StepVerifier.create(leftPop)
                .expectNext("Beta")
                .verifyComplete();
    }
}
