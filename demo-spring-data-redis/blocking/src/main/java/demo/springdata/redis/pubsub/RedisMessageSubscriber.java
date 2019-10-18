package demo.springdata.redis.pubsub;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RedisMessageSubscriber implements MessageListener {

    private static List<String> messages = new ArrayList<String>();

    public static List<String> getMessages() {
        return messages;
    }

    public void onMessage(Message message, byte[] pattern) {
        messages.add(message.toString());
        System.out.println("Message received: " + new String(message.getBody()));
    }
}