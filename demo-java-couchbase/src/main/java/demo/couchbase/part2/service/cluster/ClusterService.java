package demo.couchbase.part2.service.cluster;

import com.couchbase.client.java.Bucket;

public interface ClusterService {

    Bucket openBucket(String name, String password);
}
