package demo.redis.jedis;

import demo.redis.IntegrationTest;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class JedisIntegrationTest extends IntegrationTest {

    private static Jedis jedis;

    @BeforeClass
    public static void beforeClass() throws IOException {
        IntegrationTest.beforeClass();
        jedis = new Jedis("localhost", getRedisPort());
    }

    @AfterClass
    public static void afterClass() {
        IntegrationTest.afterClass();
    }

    @After
    public void afterMethod() {
        jedis.flushAll();
    }

    @Test
    public void testString() {
        String key = "key";
        String value = "value";

        jedis.set(key, value);

        String actualValue = jedis.get(key);
        assertEquals(value, actualValue);
    }

    @Test
    public void testList() {
        String queue = "list";

        String value1 = "Alpha";
        String value2 = "Beta";
        String value3 = "Gamma";

        jedis.lpush(queue, value1, value2);

        String actualValue1 = jedis.rpop(queue);
        assertEquals(value1, actualValue1);

        jedis.lpush(queue, value3);

        String actualValue2 = jedis.rpop(queue);
        String actualValue3 = jedis.rpop(queue);

        assertEquals(value2, actualValue2);
        assertEquals(value3, actualValue3);

        String actualValue4 = jedis.rpop(queue);
        assertNull(actualValue4);
    }

    @Test
    public void testSet() {
        String set = "set";

        String value1 = "Alpha";
        String value2 = "Beta";
        String value3 = "Beta";

        jedis.sadd(set, value1);

        Set<String> actualSet1 = jedis.smembers(set);
        assertEquals(1, actualSet1.size());

        jedis.sadd(set, value2);
        Set<String> actualSet2 = jedis.smembers(set);
        assertEquals(2, actualSet2.size());

        jedis.sadd(set, value3);
        Set<String> actualSet3 = jedis.smembers(set);
        assertEquals(2, actualSet3.size());

        boolean existsValue3 = jedis.sismember(set, value3);
        assertTrue(existsValue3);
    }

    @Test
    public void testHash() {
        String key = "key";

        String field1 = "A";
        String value1 = "Alpha";

        String field2 = "B";
        String value2 = "Beta";

        jedis.hset(key, field1, value1);
        jedis.hset(key, field2, value2);

        String actualValue1 = jedis.hget(key, field1);
        assertEquals(value1, actualValue1);

        Map<String, String> fields = jedis.hgetAll(key);
        String actualValue2 = fields.get(field2);

        assertEquals(value2, actualValue2);
    }

    @Test
    public void testSortedSet() {
        String key = "ranking";

        Map<String, Double> scores = new HashMap<>();

        scores.put("PlayerOne", 3000.0);
        scores.put("PlayerTwo", 1500.0);
        scores.put("PlayerThree", 8200.0);

        scores.entrySet().forEach(playerScore -> {
            jedis.zadd(key, playerScore.getValue(), playerScore.getKey());
        });

        Set<String> players = jedis.zrevrange(key, 0, 1);
        assertEquals("PlayerThree", players.iterator().next());

        long rank = jedis.zrevrank(key, "PlayerOne");
        assertEquals(1, rank);
    }

    @Test
    public void givenMultipleOperationsThatNeedToBeExecutedAtomically_thenWrapThemInATransaction() {
        String friendsPrefix = "friends#";

        String userOneId = "4352523";
        String userTwoId = "5552321";

        Transaction t = jedis.multi();
        t.sadd(friendsPrefix + userOneId, userTwoId);
        t.sadd(friendsPrefix + userTwoId, userOneId);
        t.exec();

        boolean exists = jedis.sismember(friendsPrefix + userOneId, userTwoId);
        Assert.assertTrue(exists);

        exists = jedis.sismember(friendsPrefix + userTwoId, userOneId);
        Assert.assertTrue(exists);
    }

    @Test
    public void givenMultipleIndependentOperations_whenNetworkOptimizationIsImportant_thenWrapThemInAPipeline() {
        String userOneId = "4352523";
        String userTwoId = "4849888";

        Pipeline p = jedis.pipelined();
        p.sadd("searched#" + userOneId, "paris");
        p.zadd("ranking", 126, userOneId);
        p.zadd("ranking", 325, userTwoId);
        Response<Boolean> pipeExists = p.sismember("searched#" + userOneId, "paris");
        Response<Set<String>> pipeRanking = p.zrange("ranking", 0, -1);
        p.sync();

        Assert.assertTrue(pipeExists.get());
        assertEquals(2, pipeRanking.get().size());
    }

    @Test
    public void givenAPoolConfiguration_thenCreateAJedisPool() {
        final JedisPoolConfig poolConfig = buildPoolConfig();

        try (JedisPool jedisPool = new JedisPool(poolConfig, "localhost", getRedisPort());
             Jedis jedis = jedisPool.getResource()) {

            // do simple operation to verify that the Jedis resource is working
            // properly
            String key = "key";
            String value = "value";

            jedis.set(key, value);
            String value2 = jedis.get(key);

            assertEquals(value, value2);

            // flush Redis
            jedis.flushAll();
        }
    }

    private JedisPoolConfig buildPoolConfig() {
        final JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(128);
        poolConfig.setMaxIdle(128);
        poolConfig.setMinIdle(16);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setMinEvictableIdleTimeMillis(Duration.ofSeconds(60).toMillis());
        poolConfig.setTimeBetweenEvictionRunsMillis(Duration.ofSeconds(30).toMillis());
        poolConfig.setNumTestsPerEvictionRun(3);
        poolConfig.setBlockWhenExhausted(true);
        return poolConfig;
    }
}
