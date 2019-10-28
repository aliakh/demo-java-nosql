package demo.cassandra.com.datastax.oss.driver.api.repository;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import com.datastax.oss.driver.api.querybuilder.insert.RegularInsert;
import com.datastax.oss.driver.api.querybuilder.schema.CreateTable;
import com.datastax.oss.driver.api.querybuilder.select.Select;
import demo.cassandra.com.datastax.oss.driver.api.domain.Video;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VideoRepository {

    private final CqlSession session;

    public VideoRepository(CqlSession session) {
        this.session = session;
    }

    public void createTable() {
        createTable(null);
    }

    public void createTable(String keyspace) {
        CreateTable createTable = SchemaBuilder.createTable("videos").ifNotExists()
                .withPartitionKey("video_id", DataTypes.UUID)
                .withColumn("title", DataTypes.TEXT)
                .withColumn("creation_date", DataTypes.TIMESTAMP);

        executeStatement(createTable.build(), keyspace);
    }

    public UUID insertVideo(Video video) {
        return insertVideo(video, null);
    }

    public UUID insertVideo(Video video, String keyspace) {
        UUID videoId = UUID.randomUUID();

        video.setId(videoId);

        RegularInsert insertInto = QueryBuilder.insertInto("videos")
                .value("video_id", QueryBuilder.bindMarker())
                .value("title", QueryBuilder.bindMarker())
                .value("creation_date", QueryBuilder.bindMarker());

        SimpleStatement insertStatement = insertInto.build();

        if (keyspace != null) {
            insertStatement = insertStatement.setKeyspace(keyspace);
        }

        PreparedStatement preparedStatement = session.prepare(insertStatement);

        BoundStatement statement = preparedStatement.bind()
                .setUuid(0, video.getId())
                .setString(1, video.getTitle())
                .setInstant(2, video.getCreationDate());

        session.execute(statement);

        return videoId;
    }

    public List<Video> selectAll() {
        return selectAll(null);
    }

    public List<Video> selectAll(String keyspace) {
        Select select = QueryBuilder.selectFrom("videos").all();
        ResultSet resultSet = executeStatement(select.build(), keyspace);

        List<Video> videos = new ArrayList<>();
        for (Row row: resultSet) {
            videos.add(new Video(row.getUuid("video_id"), row.getString("title"), row.getInstant("creation_date")));
        }
        return videos;
    }

    private ResultSet executeStatement(SimpleStatement statement, String keyspace) {
        if (keyspace != null) {
            statement.setKeyspace(CqlIdentifier.fromCql(keyspace));
        }

        return session.execute(statement);
    }
}
