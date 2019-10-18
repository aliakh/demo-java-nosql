package demo.redis;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.net.ServerSocket;

public class IntegrationTest {

    private static RedisServer redisServer;
    private static int redisPort;

    protected static int getRedisPort() {
        return redisPort;
    }

    @BeforeClass
    protected static void beforeClass() throws IOException {
        ServerSocket s = new ServerSocket(0);
        redisPort = s.getLocalPort();
        s.close();

        redisServer = new RedisServer(redisPort);
        redisServer.start();
    }

    @AfterClass
    protected static void afterClass() {
        redisServer.stop();
    }
}
