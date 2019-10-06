package demo.couchbase.part1;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;

public class DemoAsynch {

    public static void main(String[] args) throws InterruptedException {
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

        bucket.async()
                .upsert(created)
                .doOnNext(upserted -> System.out.printf("Upserted with CAS: %s vs %s\n", upserted.cas(), created.cas()))
                .toBlocking().single();

        bucket.async()
                .get(id)
                .subscribe(found -> {
                    System.out.println("Found: " + found);
                    System.out.println("Age: " + found.content().getInt("age"));
                });

        CountDownLatch latch = new CountDownLatch(1);
        bucket.async()
                .get(id)
                .flatMap(found -> {
                    found.content().put("age", 52);
                    return bucket.async().replace(found);
                })
                .subscribe(replaced -> {
                    System.out.println("Replaced: " + replaced);
                    latch.countDown();
                });
        latch.await();

        bucket.remove(id);

        bucket.async()
                .close()
                .subscribe(closed -> cluster.disconnect());
    }
}
