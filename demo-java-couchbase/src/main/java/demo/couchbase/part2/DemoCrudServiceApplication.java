package demo.couchbase.part2;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import demo.couchbase.part2.domain.User;
import demo.couchbase.part2.service.bucket.BucketService;
import demo.couchbase.part2.service.bucket.UserBucketServiceImpl;
import demo.couchbase.part2.service.cluster.ClusterService;
import demo.couchbase.part2.service.cluster.ClusterServiceImpl;
import demo.couchbase.part2.service.crud.CrudService;
import demo.couchbase.part2.service.crud.UserCrudServiceImpl;
import demo.couchbase.part2.service.json.JsonConverter;
import demo.couchbase.part2.service.json.UserJsonConverterImpl;

public class DemoCrudServiceApplication {

    public static void main(String[] args) {
        Cluster cluster = CouchbaseCluster.create("localhost");
        ClusterService clusterService = new ClusterServiceImpl(cluster);
        BucketService bucketService = new UserBucketServiceImpl(clusterService);
        JsonConverter<User> jsonConverter = new UserJsonConverterImpl();
        CrudService<User> crudService = new UserCrudServiceImpl(bucketService, jsonConverter);

        User user = new User();
        user.setFirstName("Walter");
        user.setLastName("White");
        user.setJob("chemistry teacher");
        user.setAge(50);

        crudService.create(user);
    }
}
