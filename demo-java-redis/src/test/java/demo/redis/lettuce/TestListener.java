package demo.redis.lettuce;

import io.lettuce.core.pubsub.RedisPubSubListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestListener implements RedisPubSubListener<String, String> {

    private static final Logger logger = LoggerFactory.getLogger(TestListener.class);

    private String message;

    public String getMessage() {
        return message;
    }

    @Override
    public void message(String channel, String message) {
        logger.info("message: channel={}, message={}", channel, message);
        this.message = message;
    }

    @Override
    public void message(String pattern, String channel, String message) {
        logger.info("message: pattern={}, channel={}, message={}", pattern, channel, message);
    }

    @Override
    public void subscribed(String channel, long count) {
        logger.info("subscribed: channel={}, count={}", channel, count);
    }

    @Override
    public void psubscribed(String pattern, long count) {
        logger.info("pattern-subscribed: channel={}, count={}", pattern, count);
    }

    @Override
    public void unsubscribed(String channel, long count) {
        logger.info("unsubscribed: channel={}, count={}", channel, count);
    }

    @Override
    public void punsubscribed(String pattern, long count) {
        logger.info("pattern-unsubscribed: pattern={}, count={}", pattern, count);
    }
}
