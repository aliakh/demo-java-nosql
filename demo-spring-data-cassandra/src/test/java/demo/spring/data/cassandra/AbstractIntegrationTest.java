package demo.spring.data.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import demo.spring.data.cassandra.model.Book;
import org.apache.cassandra.exceptions.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.thrift.transport.TTransportException;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraAdminOperations;
import org.springframework.data.cassandra.core.cql.CqlIdentifier;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

abstract public class AbstractIntegrationTest {

    private static final String CREATE_KEYSPACE = "CREATE KEYSPACE IF NOT EXISTS TestKeySpace WITH replication = { 'class': 'SimpleStrategy', 'replication_factor': '3' };";
    private static final String USE_KEYSPACE = "USE TestKeySpace;";
    protected static final String TABLE_NAME = "book";

    @Autowired
    private CassandraAdminOperations cassandraAdminOperations;

    @BeforeClass
    public static void startEmbeddedCassandra() throws InterruptedException, TTransportException, ConfigurationException, IOException {
        EmbeddedCassandraServerHelper.startEmbeddedCassandra();
        Cluster cluster = Cluster.builder().addContactPoints("127.0.0.1").withPort(9142).build();
        Session session = cluster.connect();
        session.execute(CREATE_KEYSPACE);
        session.execute(USE_KEYSPACE);
        Thread.sleep(5000);
    }

    @AfterClass
    public static void stopEmbeddedCassandra() {
        EmbeddedCassandraServerHelper.cleanEmbeddedCassandra();
    }

    @Before
    public void createTable() {
        cassandraAdminOperations.createTable(true, CqlIdentifier.cqlId(TABLE_NAME), Book.class, new HashMap<String, Object>());
    }

    @After
    public void dropTable() {
        cassandraAdminOperations.dropTable(CqlIdentifier.cqlId(TABLE_NAME));
    }
}
