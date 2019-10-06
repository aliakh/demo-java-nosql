package demo.part1;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;

import java.util.UUID;

public class Main {

    public static void main(String[] args) {
        Cluster cluster = CouchbaseCluster.create("localhost");
        Bucket bucket = cluster.openBucket("demo bucket");

        JsonObject content = JsonObject.empty()
                .put("name", "John Doe")
                .put("type", "Person")
                .put("email", "john.doe@mydomain.com")
                .put("homeTown", "Chicago");

        String id = UUID.randomUUID().toString();
        JsonDocument document = JsonDocument.create(id, content);

        JsonDocument inserted = bucket.insert(document);

        JsonDocument upserted = bucket.upsert(document);

        JsonDocument retrieved = bucket.get(id);

        JsonObject content2 = document.content();
        content2.put("homeTown", "Kansas City");
        JsonDocument upserted2 = bucket.upsert(document);

        JsonDocument removed = bucket.remove(document);

        JsonDocument removed2 = bucket.remove(id);
    }
}
