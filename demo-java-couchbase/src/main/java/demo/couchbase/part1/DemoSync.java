package demo.couchbase.part1;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;

import java.util.UUID;

public class DemoSync {

    public static void main(String[] args) {
        Cluster cluster = CouchbaseCluster.create("localhost");

        cluster.authenticate("users", "password");

        Bucket bucket = cluster.openBucket("users");

        JsonObject user = JsonObject.empty()
                .put("firstName", "Walter")
                .put("lastName", "White")
                .put("job", "chemistry teacher")
                .put("age", 50);

        final String id = UUID.randomUUID().toString();
        JsonDocument created = JsonDocument.create(id, user);
        System.out.println("Created: " + created);

        JsonDocument upserted = bucket.upsert(created);
        System.out.printf("Upserted with CAS: %s vs %s\n", upserted.cas(), created.cas());

        JsonDocument found = bucket.get(id);
        System.out.println("Found: " + found);
        System.out.println("Age: " + found.content().getInt("age"));

        found.content().put("age", 52);

        JsonDocument replaced = bucket.replace(found);
        System.out.println("Replaced: " + replaced);

        bucket.remove(id);

        cluster.disconnect();
    }
}
