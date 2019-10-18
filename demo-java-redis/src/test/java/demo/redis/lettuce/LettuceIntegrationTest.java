package demo.redis.lettuce;

import demo.redis.IntegrationTest;
import io.lettuce.core.LettuceFutures;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.TransactionResult;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LettuceIntegrationTest extends IntegrationTest {

    private static RedisClient client;
    private static StatefulRedisConnection<String, String> connection;

    @BeforeClass
    public static void beforeClass() throws IOException {
        IntegrationTest.beforeClass();

        client = RedisClient.create("redis://localhost:" + getRedisPort() + "/");
        connection = client.connect();
    }

    @AfterClass
    public static void afterClass() {
        connection.close();

        IntegrationTest.afterClass();
    }

    @Test
    public void testStringSync() {
        String key = "key";
        String value = "value";

        RedisCommands<String, String> syncCommands = connection.sync();
        syncCommands.set(key, value);

        String actualValue = syncCommands.get(key);

        assertEquals(value, actualValue);
    }

    @Test
    public void testHashSync() {
        String key = "list";

        String field1 = "A";
        String value1 = "Alpha";

        String field2 = "B";
        String value2 = "Beta";

        RedisCommands<String, String> syncCommands = connection.sync();
        syncCommands.hset(key, field1, value1);
        syncCommands.hset(key, field2, value2);

        Map<String, String> fieldsToValues = syncCommands.hgetall(key);

        assertEquals(value1, fieldsToValues.get(field1));
        assertEquals(value2, fieldsToValues.get(field2));
    }

    @Test
    public void testStringAsync() throws Exception {
        String key = "key";
        String value = "value";

        RedisAsyncCommands<String, String> asyncCommands = connection.async();
        asyncCommands.set(key, value);

        RedisFuture<String> actualValueFuture = asyncCommands.get(key);

        String actualValue = actualValueFuture.get();
        assertEquals(value, actualValue);
    }

    @Test
    public void testHashAsync() throws Exception {
        String key = "hash";

        String field1 = "A";
        String value1 = "Alpha";

        String field2 = "B";
        String value2 = "Beta";

        RedisAsyncCommands<String, String> asyncCommands = connection.async();
        asyncCommands.hset(key, field1, value1);
        asyncCommands.hset(key, field2, value2);

        RedisFuture<Map<String, String>> fieldsToValuesFuture = asyncCommands.hgetall(key);

        Map<String, String> fieldsToValues = fieldsToValuesFuture.get();

        assertEquals(value1, fieldsToValues.get(field1));
        assertEquals(value2, fieldsToValues.get(field2));
    }

    @Test
    public void testListAsync() throws Exception {
        String key = "list";

        String value1 = "Alpha";
        String value2 = "Beta";

        RedisAsyncCommands<String, String> asyncCommands = connection.async();
        asyncCommands.del(key);

        asyncCommands.lpush(key, value1);
        asyncCommands.lpush(key, value2);

        RedisFuture<String> valueFuture1 = asyncCommands.rpop(key);
        String actualValue1 = valueFuture1.get();

        assertEquals(value1, actualValue1);

        asyncCommands.del(key);

        asyncCommands.lpush(key, value1);
        asyncCommands.lpush(key, value2);

        RedisFuture<String> valueFuture2 = asyncCommands.lpop(key);
        String actualValue2 = valueFuture2.get();

        assertEquals(value2, actualValue2);

    }

    @Test
    public void testSetAsync() throws Exception {
        String key = "set";

        String value1 = "Alpha";
        String value2 = "Beta";
        String value3 = "Beta";

        RedisAsyncCommands<String, String> asyncCommands = connection.async();
        asyncCommands.sadd(key, value1);

        RedisFuture<Set<String>> actualSetFuture1 = asyncCommands.smembers(key);
        assertEquals(1, actualSetFuture1.get().size());

        asyncCommands.sadd(key, value2);
        RedisFuture<Set<String>> actualSetFuture2 = asyncCommands.smembers(key);
        assertEquals(2, actualSetFuture2.get().size());

        asyncCommands.sadd(key, value3);
        RedisFuture<Set<String>> actualSetFuture3 = asyncCommands.smembers(key);
        assertEquals(2, actualSetFuture3.get().size());

        RedisFuture<Boolean> value3ExistsFuture = asyncCommands.sismember(key, value3);
        assertTrue(value3ExistsFuture.get());
    }

    @Test
    public void testSortedSetAsync() throws Exception {
        String key = "sorted set";

        String value1 = "Alpha";
        String value2 = "Beta";
        String value3 = "Gamma";

        RedisAsyncCommands<String, String> asyncCommands = connection.async();
        asyncCommands.zadd(key, 100.0, value3);
        asyncCommands.zadd(key, 300.0, value1);
        asyncCommands.zadd(key, 200.0, value2);

        RedisFuture<List<String>> values1Future = asyncCommands.zrevrange(key, 0, 3);
        assertEquals(value1, values1Future.get().get(0));

        RedisFuture<List<String>> values2Future = asyncCommands.zrange(key, 0, 3);
        assertEquals(value3, values2Future.get().get(0));
    }

    @Test
    public void testTransactionAsync() throws Exception {
        RedisAsyncCommands<String, String> asyncCommands = connection.async();

        asyncCommands.multi();

        RedisFuture<String> future1 = asyncCommands.set("key1", "value1");
        RedisFuture<String> future2 = asyncCommands.set("key2", "value2");
        RedisFuture<String> future3 = asyncCommands.set("key3", "value3");

        RedisFuture<TransactionResult> transactionResultFuture = asyncCommands.exec();
        TransactionResult transactionResult = transactionResultFuture.get();

        String result1 = transactionResult.get(0);
        String result2 = transactionResult.get(0);
        String result3 = transactionResult.get(0);

        final String resultOK = "OK";

        assertEquals(resultOK, result1);
        assertEquals(resultOK, result2);
        assertEquals(resultOK, result3);

        assertEquals(resultOK, future1.get());
        assertEquals(resultOK, future2.get());
        assertEquals(resultOK, future3.get());
    }

    @Test
    public void testFlushAsync() {
        RedisAsyncCommands<String, String> asyncCommands = connection.async();

        asyncCommands.setAutoFlushCommands(false);

        List<RedisFuture<?>> futuresList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            futuresList.add(asyncCommands.set("key" + i, "value" + i));
        }

        asyncCommands.flushCommands();

        RedisFuture[] futuresArray = futuresList.toArray(new RedisFuture[0]);

        boolean result = LettuceFutures.awaitAll(1, TimeUnit.SECONDS, futuresArray);
        assertTrue(result);

        asyncCommands.setAutoFlushCommands(true);
    }

    @Test
    @Ignore
    public void testPubSub() throws Exception {
        StatefulRedisPubSubConnection<String, String> subscriberConnection = client.connectPubSub();
        StatefulRedisPubSubConnection<String, String> publisherConnection = client.connectPubSub();

        TestListener listener = new TestListener();
        subscriberConnection.addListener(listener);

        String channel = "Channel";
        String message = "Message";

        RedisPubSubAsyncCommands<String, String> subscriber = subscriberConnection.async();
        RedisPubSubAsyncCommands<String, String> publisher = publisherConnection.async();

        RedisFuture<Void> subscribeFuture = subscriber.subscribe(channel);
        subscribeFuture.get(1, TimeUnit.SECONDS); // use CompletableFuture.thenCompose

        RedisFuture<Long> publishFuture = publisher.publish(channel, message);
        long recipientsCount = publishFuture.get(1, TimeUnit.SECONDS);

        assertEquals(1, recipientsCount);
        assertEquals(message, listener.getMessage());
    }
}
