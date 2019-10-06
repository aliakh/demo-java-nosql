package demo.couchbase.part2.service.json;

import com.couchbase.client.java.document.JsonDocument;

public interface JsonConverter<T> {

    JsonDocument toDocument(T t);

    T fromDocument(JsonDocument doc);
}
