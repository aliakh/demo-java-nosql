package demo.couchbase.part2.service.cluster;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClusterServiceImpl implements ClusterService {

    private final Cluster cluster;
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public ClusterServiceImpl(Cluster cluster) {
        this.cluster = cluster;
    }

    @Override
    synchronized public Bucket openBucket(String name, String password) {
        if (!buckets.containsKey(name)) {
            Bucket bucket = cluster.openBucket(name, password);
            buckets.put(name, bucket);
        }
        return buckets.get(name);
    }
}
