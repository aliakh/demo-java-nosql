package demo.spring.data.cassandra.model;

import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;
import java.util.UUID;

import org.springframework.cassandra.core.Ordering;
import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.mapping.Table;

@Table
public class Book {

    @PrimaryKeyColumn(name = "id", ordinal = 0, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    private UUID id;

    @PrimaryKeyColumn(name = "title", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    private String title;

    @PrimaryKeyColumn(name = "publisher", ordinal = 2, type = PrimaryKeyType.PARTITIONED)
    private String publisher;

    @Column
    private Set<String> tags = new HashSet<>();

    public Book( UUID id,  String title,  String publisher,  Set<String> tags) {
        this.id = id;
        this.title = title;
        this.publisher = publisher;
        this.tags.addAll(tags);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Book.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("title='" + title + "'")
                .add("publisher='" + publisher + "'")
                .add("tags=" + tags)
                .toString();
    }
}
