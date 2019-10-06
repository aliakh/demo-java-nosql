package demo.couchbase.part2.service.bucket;

import com.couchbase.client.java.Bucket;
import demo.couchbase.part2.service.cluster.ClusterService;

public abstract class AbstractBucketServiceImpl implements BucketService {

    private ClusterService clusterService;
    private Bucket bucket;

    public AbstractBucketServiceImpl(ClusterService clusterService) {
        this.clusterService = clusterService;
    }

    protected void openBucket() {
        bucket = clusterService.openBucket(getBucketName(), getBucketPassword());
    }

    protected abstract String getBucketName();

    protected abstract String getBucketPassword();

    @Override
    public Bucket getBucket() {
        return bucket;
    }
}
