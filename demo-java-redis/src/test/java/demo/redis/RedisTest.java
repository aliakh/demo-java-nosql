package demo.redis;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.net.ServerSocket;

public class RedisTest {

    static private RedisServer redisServer;
    static protected int port;

    @BeforeClass
    static public void postConstruct() throws IOException {
        ServerSocket s = new ServerSocket(0);
        port = s.getLocalPort();
        s.close();

        redisServer = new RedisServer(port);
        redisServer.start();
    }

    @AfterClass
    static public void preDestroy() {
        redisServer.stop();
    }
}
