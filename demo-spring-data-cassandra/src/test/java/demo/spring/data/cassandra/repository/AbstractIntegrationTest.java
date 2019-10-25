package demo.spring.data.cassandra.repository;

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
import org.springframework.cassandra.core.cql.CqlIdentifier;
import org.springframework.data.cassandra.core.CassandraAdminOperations;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

abstract public class AbstractIntegrationTest {

    private static final Log LOGGER = LogFactory.getLog(AbstractIntegrationTest.class);

    private static final String CREATE_KEYSPACE = "CREATE KEYSPACE IF NOT EXISTS TestKeySpace WITH replication = { 'class': 'SimpleStrategy', 'replication_factor': '3' };";
    private static final String USE_KEYSPACE = "USE TestKeySpace;";
    private static final String TABLE_NAME = "book";

    @Autowired
    private CassandraAdminOperations cassandraAdminOperations;

    @BeforeClass
    public static void startEmbeddedCassandra() throws InterruptedException, TTransportException, ConfigurationException, IOException {
        LOGGER.info("Start embedded Cassandra");
        EmbeddedCassandraServerHelper.startEmbeddedCassandra();
        LOGGER.info("Connect co cluster");
        Cluster cluster = Cluster.builder().addContactPoints("127.0.0.1").withPort(9142).build();
        Session session = cluster.connect();
        LOGGER.info("Prepare key space");
        session.execute(CREATE_KEYSPACE);
        session.execute(USE_KEYSPACE);
        LOGGER.info("Waiting started");
        Thread.sleep(5000);
        LOGGER.info("Waiting finished");
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
