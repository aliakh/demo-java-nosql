package demo.springdata.redis;

import demo.springdata.redis.model.Person;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.time.Duration;

import static demo.springdata.redis.model.Person.Gender.FEMALE;
import static demo.springdata.redis.model.Person.Gender.MALE;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SpringDataRedisReactiveApplication.class)
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class ReactiveValueOperationsIntegrationTest {

    private static RedisServer redisServer;

    @Autowired
    private ReactiveRedisTemplate<String, Person> redisTemplate;

    private ReactiveValueOperations<String, Person> reactiveValueOps;

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
        reactiveValueOps = redisTemplate.opsForValue();
    }

    @Test
    public void testSet() {
        Mono<Boolean> result = reactiveValueOps.set("1", new Person("1", "John Doe", MALE, 1999));

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    public void testGet() {
        Mono<Person> actualPerson = reactiveValueOps.get("1");

        StepVerifier.create(actualPerson)
                .expectNext(new Person("1", "John Doe", MALE, 1999))
                .verifyComplete();
    }

    @Test
    public void testSetGet() throws InterruptedException {
        Mono<Boolean> result = reactiveValueOps.set("2", new Person("2", "Janie Doe", FEMALE, 2001), Duration.ofSeconds(1));

        Mono<Person> actualPerson = reactiveValueOps.get("2");

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();

        Thread.sleep(1000L);

        StepVerifier.create(actualPerson)
                .expectNextCount(0L)
                .verifyComplete();
    }
}
