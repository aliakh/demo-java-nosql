package demo.redis.redisson;

import demo.redis.IntegrationTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.redisson.Redisson;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBatch;
import org.redisson.api.RBucket;
import org.redisson.api.RKeys;
import org.redisson.api.RList;
import org.redisson.api.RLiveObjectService;
import org.redisson.api.RMap;
import org.redisson.api.RRemoteService;
import org.redisson.api.RScript;
import org.redisson.api.RSet;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RedissonIntegrationTest extends IntegrationTest {

    private static RedissonClient client;

    @BeforeClass
    public static void beforeClass() throws IOException {
        IntegrationTest.beforeClass();

        Config config = new Config();
        config.useSingleServer().setAddress("localhost:" + getRedisPort());
        client = Redisson.create(config);
    }

    @AfterClass
    public static void afterClass() {
        client.shutdown();

        IntegrationTest.afterClass();
    }

    @Test
    public void testGetKeys() {
        client.getBucket("key1").set("value1");
        client.getBucket("key2").set("value2");
        client.getBucket("key3").set("value3");

        RKeys rkeys = client.getKeys();

        assertTrue(rkeys.count() >= 3);
    }

    @Test
    public void testGetKeysByPattern() {
        client.getBucket("key1").set("value1");
        client.getBucket("key2").set("value2");
        client.getBucket("key3").set("value3");
        client.getBucket("id4").set("value4");

        RKeys rkeys = client.getKeys();

        Iterable<String> keysIterator = rkeys.getKeysByPattern("key*");

        List<String> keys = StreamSupport.stream(keysIterator.spliterator(), false)
                .collect(Collectors.toList());

        assertEquals(3, keys.size());
    }

    @Test
    public void testBucket() {
        RBucket<Message> bucket = client.getBucket("Bucket");

        Message message = new Message();
        message.setText("bucket text");
        bucket.set(message);

        Message actualMessage = bucket.get();

        assertEquals(message.getText(), actualMessage.getText());
    }

    @Test
    public void testAtomicLong() {
        long value = 1L;

        RAtomicLong atomicLong = client.getAtomicLong("Counter");
        atomicLong.set(value);
        long actualValue = atomicLong.incrementAndGet();

        assertEquals(2, actualValue);
    }

    @Test
    public void testPubSub() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = new CompletableFuture<>();

        String topic = "Topic";
        RTopic<Message> subscribeTopic = client.getTopic(topic);
        subscribeTopic.addListener((channel, message) -> future.complete(message.getText()));

        RTopic<Message> publishTopic = client.getTopic(topic);
        long recipientsCount = publishTopic.publish(new Message("pub-sub text"));

        assertEquals(1, recipientsCount);
        assertEquals("pub-sub text", future.get());
    }

    @Test
    public void testMap() {
        RMap<String, Message> map = client.getMap("Map");

        map.put("key", new Message("map text"));

        Message actualMessage = map.get("key");
        assertEquals("map text", actualMessage.getText());
    }

    @Test
    public void givenASet_thenSaveSetToRedis() {
        RSet<Message> set = client.getSet("Set");
        set.add(new Message("set text"));

        assertTrue(set.contains(new Message("set text")));
    }

    @Test
    public void testList() {
        RList<Message> list = client.getList("List");

        list.add(new Message("list text"));

        assertTrue(list.contains(new Message("list text")));
    }

    @Test
    public void testRemoteService() {
        RRemoteService remoteService = client.getRemoteService();
        remoteService.register(TestService.class, new TestServiceImpl());

        TestService testService = remoteService.get(TestService.class);

        int actualNumber = testService.getNumber();
        assertEquals(1, actualNumber);
    }

    @Test
    public void testLiveObjectService() {
        MessageEntity message = new MessageEntity();
        message.setId("message id");

        RLiveObjectService service = client.getLiveObjectService();
        message = service.persist(message);

        MessageEntity actualMessage = service.get(MessageEntity.class, "message id");
        assertEquals(message.getId(), actualMessage.getId());
    }

    @Test
    public void testBatch() {
        RBatch batch = client.createBatch();

        String map = "Map";
        batch.getMap(map).fastPutAsync("A", "Alpha");
        batch.getMap(map).putAsync("B", "Beta");

        List<?> result = batch.execute();

        RMap<String, String> actualMap = client.getMap(map);
        assertEquals("Alpha", actualMap.get("A"));
        assertEquals("Beta", actualMap.get("B"));
    }

    @Test
    public void testLuaScript() {
        client.getBucket("key").set("value");

        String luaScript = "return redis.call('get', 'key')";
        String actualValue = client.getScript().eval(RScript.Mode.READ_ONLY, luaScript, RScript.ReturnType.VALUE);

        assertEquals("value", actualValue);
    }
}
