package demo.couchbase.part2.service.crud;

import demo.couchbase.part2.domain.User;
import demo.couchbase.part2.service.bucket.BucketService;
import demo.couchbase.part2.service.json.JsonConverter;

public class UserCrudServiceImpl extends AbstractCrudServiceImpl<User> {

    public UserCrudServiceImpl(BucketService bucketService, JsonConverter<User> jsonConverter) {
        super(bucketService, jsonConverter);
        loadBucket();
    }
}
