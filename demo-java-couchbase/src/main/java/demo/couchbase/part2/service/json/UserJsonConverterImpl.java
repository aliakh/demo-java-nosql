package demo.couchbase.part2.service.json;

import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import demo.couchbase.part2.domain.User;

public class UserJsonConverterImpl implements JsonConverter<User> {

    @Override
    public JsonDocument toDocument(User user) {
        JsonObject content = JsonObject.empty()
                .put("firstName", user.getFirstName())
                .put("lastName", user.getLastName())
                .put("job", user.getJob())
                .put("age", user.getAge());
        return JsonDocument.create(user.getId(), content);
    }

    @Override
    public User fromDocument(JsonDocument doc) {
        JsonObject content = doc.content();
        User user = new User();
        user.setId(doc.id());
        user.setFirstName(content.getString("firstName"));
        user.setLastName(content.getString("lastName"));
        user.setJob(content.getString("job"));
        user.setAge(content.getInt("age"));
        return user;
    }
}
