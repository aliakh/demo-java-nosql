package demo_.mapreduce;

public interface CouchbaseKeyGenerator<T> {

    String generateKey(T t);
}
