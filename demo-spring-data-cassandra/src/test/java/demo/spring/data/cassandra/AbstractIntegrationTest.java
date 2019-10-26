package demo.spring.data.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import demo.spring.data.cassandra.model.Book;
import org.apache.cassandra.exceptions.ConfigurationException;
import org.apache.thrift.transport.TTransportException;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cassandra.core.cql.CqlIdentifier;
import org.springframework.data.cassandra.core.CassandraAdminOperations;

import java.io.IOException;
import java.util.HashMap;

abstract public class AbstractIntegrationTest {

    private static final String CREATE_KEYSPACE = "CREATE KEYSPACE IF NOT EXISTS BookKeySpace WITH replication = { 'class': 'SimpleStrategy', 'replication_factor': '3' };";
    private static final String USE_KEYSPACE = "USE BookKeySpace;";
    private static final String TABLE_NAME = "book";

    protected static final String TITLE1 = "Learning Java";
    protected static final String TITLE2 = "Java in a Nutshell";
    protected static final String PUBLISHER = "O'Reilly Media";
    protected static final String TAG1 = "Software";
    protected static final String TAG2 = "Java";

    @Autowired
    private CassandraAdminOperations adminTemplate;

    @BeforeClass
    protected static void startEmbeddedCassandra() throws InterruptedException, TTransportException, ConfigurationException, IOException {
        EmbeddedCassandraServerHelper.startEmbeddedCassandra();
        Cluster cluster = Cluster.builder().addContactPoints("127.0.0.1").withPort(9142).build();
        Session session = cluster.connect();
        session.execute(CREATE_KEYSPACE);
        session.execute(USE_KEYSPACE);
        Thread.sleep(5000);
    }

    @AfterClass
    protected static void stopEmbeddedCassandra() {
        EmbeddedCassandraServerHelper.cleanEmbeddedCassandra();
    }

    @Before
    protected void createTable() {
        adminTemplate.createTable(true, CqlIdentifier.cqlId(TABLE_NAME), Book.class, new HashMap<String, Object>());
    }

    @After
    protected void dropTable() {
        adminTemplate.dropTable(CqlIdentifier.cqlId(TABLE_NAME));
    }

    protected static String getTableName() {
        return TABLE_NAME;
    }
}
