package demo_.mapreduce;

import java.util.UUID;

public class RandomUUIDGenerator<T> implements CouchbaseKeyGenerator<T> {

    @Override
    public String generateKey(T t) {
        return UUID.randomUUID().toString();
    }
}
