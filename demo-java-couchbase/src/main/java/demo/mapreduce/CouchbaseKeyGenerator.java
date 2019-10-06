package demo.mapreduce;

public interface CouchbaseKeyGenerator<T> {

    String generateKey(T t);
}
