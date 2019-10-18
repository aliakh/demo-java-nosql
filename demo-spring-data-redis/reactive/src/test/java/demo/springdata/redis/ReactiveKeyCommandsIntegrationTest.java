package demo.springdata.redis;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.ReactiveKeyCommands;
import org.springframework.data.redis.connection.ReactiveStringCommands;
import org.springframework.data.redis.connection.ReactiveStringCommands.SetCommand;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.nio.ByteBuffer;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SpringDataRedisReactiveApplication.class)
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class ReactiveKeyCommandsIntegrationTest {

    private static RedisServer redisServer;

    @Autowired
    private ReactiveKeyCommands keyCommands;

    @Autowired
    private ReactiveStringCommands stringCommands;

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
    public void testKeys() {
        Flux<String> keys = Flux.just("key1", "key2", "key3", "key4");

        Flux<SetCommand> generator = keys.map(String::getBytes)
                .map(ByteBuffer::wrap)
                .map(key -> SetCommand.set(key)
                        .value(key));

        StepVerifier.create(stringCommands.set(generator))
                .expectNextCount(4L)
                .verifyComplete();

        Mono<Long> keyCount = keyCommands.keys(ByteBuffer.wrap("key*".getBytes()))
                .flatMapMany(Flux::fromIterable)
                .count();

        StepVerifier.create(keyCount)
                .expectNext(4L)
                .verifyComplete();
    }
}
