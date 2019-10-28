package demo.cassandra.com.datastax.oss.driver.api;

import com.datastax.oss.driver.api.core.CqlSession;
import demo.cassandra.com.datastax.oss.driver.api.domain.Video;
import demo.cassandra.com.datastax.oss.driver.api.repository.KeyspaceRepository;
import demo.cassandra.com.datastax.oss.driver.api.repository.VideoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class CassandraClient {

    private static final Logger LOG = LoggerFactory.getLogger(CassandraClient.class);

    public static void main(String[] args) {
        CassandraConnector connector = new CassandraConnector();
        connector.connect("127.0.0.1", 9042, "datacenter1");
        CqlSession session = connector.getSession();

        KeyspaceRepository keyspaceRepository = new KeyspaceRepository(session);

        keyspaceRepository.createKeyspace("testKeyspace", 1);
        keyspaceRepository.useKeyspace("testKeyspace");

        VideoRepository videoRepository = new VideoRepository(session);

        videoRepository.createTable();

        videoRepository.insertVideo(new Video("Video Title 1", Instant.now()));
        videoRepository.insertVideo(new Video("Video Title 2", Instant.now().minus(1, ChronoUnit.DAYS)));

        List<Video> videos = videoRepository.selectAll();

        videos.forEach(video -> LOG.info(video.toString()));

        connector.close();
    }
}
