package demo.redis.jedis;

import demo.redis.IntegrationTest;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class JedisIntegrationTest extends IntegrationTest {

    private static Jedis client;

    @BeforeClass
    public static void beforeClass() throws IOException {
        IntegrationTest.beforeClass();

        client = new Jedis("localhost", getRedisPort());
    }

    @AfterClass
    public static void afterClass() {
        IntegrationTest.afterClass();
    }

    @After
    public void afterMethod() {
        client.flushAll();
    }

    @Test
    public void testString() {
        String key = "key";
        String value = "value";

        client.set(key, value);

        String actualValue = client.get(key);
        assertEquals(value, actualValue);
    }

    @Test
    public void testList() {
        String key = "list";

        String value1 = "Alpha";
        String value2 = "Beta";
        String value3 = "Gamma";

        client.lpush(key, value1, value2);

        String actualValue1 = client.rpop(key);
        assertEquals(value1, actualValue1);

        client.lpush(key, value3);

        String actualValue2 = client.rpop(key);
        String actualValue3 = client.rpop(key);

        assertEquals(value2, actualValue2);
        assertEquals(value3, actualValue3);

        String actualValue4 = client.rpop(key);
        assertNull(actualValue4);
    }

    @Test
    public void testSet() {
        String key = "set";

        String value1 = "Alpha";
        String value2 = "Beta";
        String value3 = "Beta";

        client.sadd(key, value1);

        Set<String> actualSet1 = client.smembers(key);
        assertEquals(1, actualSet1.size());

        client.sadd(key, value2);
        Set<String> actualSet2 = client.smembers(key);
        assertEquals(2, actualSet2.size());

        client.sadd(key, value3);
        Set<String> actualSet3 = client.smembers(key);
        assertEquals(2, actualSet3.size());

        boolean value3Exists = client.sismember(key, value3);
        assertTrue(value3Exists);
    }

    @Test
    public void testHash() {
        String key = "hash";

        String field1 = "A";
        String value1 = "Alpha";

        String field2 = "B";
        String value2 = "Beta";

        client.hset(key, field1, value1);
        client.hset(key, field2, value2);

        String actualValue1 = client.hget(key, field1);
        assertEquals(value1, actualValue1);

        Map<String, String> fields = client.hgetAll(key);
        String actualValue2 = fields.get(field2);

        assertEquals(value2, actualValue2);
    }

    @Test
    public void testSortedSet() {
        String key = "sorted set";

        String value1 = "Alpha";
        String value2 = "Beta";
        String value3 = "Gamma";

        client.zadd(key, 2.0, value1);
        client.zadd(key, 1.0, value2);
        client.zadd(key, 3.0, value3);

        Set<String> actualSet = client.zrevrange(key, 0, 1);
        assertEquals(value3, actualSet.iterator().next());

        long value1Rank = client.zrevrank(key, value1);
        assertEquals(1, value1Rank);
    }

    @Test
    public void testTransaction() {
        String prefix = "key";

        String suffix1 = "1";
        String suffix2 = "2";

        String key1 = prefix + suffix1;
        String key2 = prefix + suffix2;

        Transaction transaction = client.multi();
        transaction.sadd(key1, suffix1);
        transaction.sadd(key2, suffix2);
        transaction.exec();

        boolean member1Exists = client.sismember(key1, suffix1);
        assertTrue(member1Exists);

        boolean member2Exists = client.sismember(key2, suffix2);
        assertTrue(member2Exists);
    }

    @Test
    public void testPipeline() {
        String key1 = "key1";
        String value1 = "value";

        String key2 = "key2";
        String value20 = "value20";
        String value21 = "value21";

        Pipeline pipeline = client.pipelined();

        pipeline.sadd(key1, value1);
        pipeline.zadd(key2, 1.0, value20);
        pipeline.zadd(key2, 2.0, value21);
        Response<Boolean> value1ExistsResponse = pipeline.sismember(key1, value1);
        Response<Set<String>> expectedSetResponse = pipeline.zrange(key2, 0, -1);

        pipeline.sync();

        assertTrue(value1ExistsResponse.get());

        int size = expectedSetResponse.get().size();
        assertEquals(2, size);
    }
}
