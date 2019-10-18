package demo.springdata.redis.pubsub;

public interface MessagePublisher {

    void publish(String message);
}
