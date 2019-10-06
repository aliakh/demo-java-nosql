package demo.couchbase.part2.service.bucket;

import demo.couchbase.part2.service.cluster.ClusterService;

public class UserBucketServiceImpl extends AbstractBucketServiceImpl {

    public UserBucketServiceImpl(ClusterService clusterService) {
        super(clusterService);
        openBucket();
    }

    @Override
    protected String getBucketName() {
        return "users";
    }

    @Override
    protected String getBucketPassword() {
        return "password";
    }
}
