package demo.spring.data.cassandra.repository;

import static junit.framework.TestCase.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import demo.spring.data.cassandra.AbstractIntegrationTest;
import org.apache.cassandra.exceptions.ConfigurationException;
import org.apache.thrift.transport.TTransportException;
import demo.spring.data.cassandra.config.CassandraConfig;
import demo.spring.data.cassandra.model.Book;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cassandra.core.cql.CqlIdentifier;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.core.utils.UUIDs;
import com.google.common.collect.ImmutableSet;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CassandraConfig.class)
public class CqlQueriesIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private CassandraOperations cassandraOperations;

    @BeforeClass
    public static void beforeClass() throws Exception {
        startEmbeddedCassandra();
    }

    @AfterClass
    public static void afterClass() {
        stopEmbeddedCassandra();
    }

    @Before
    public void beforeMethod() {
        createTable();
    }

    @After
    public void afterMethod() {
        dropTable();
    }

    @Test
    public void whenSavingBook_thenAvailableOnRetrieval_usingQueryBuilder() {
        UUID uuid = UUIDs.timeBased();
        Insert insert = QueryBuilder.insertInto(getTableName()).value("id", uuid).value("title", "Head First Java").value("publisher", "OReilly Media").value("tags", ImmutableSet.of("Software"));
        cassandraOperations.execute(insert);
        Select select = QueryBuilder.select().from("book").limit(10);
        Book actualBook = cassandraOperations.selectOne(select, Book.class);
        assertEquals(uuid, actualBook.getId());
    }

    @Test
    public void whenSavingBook_thenAvailableOnRetrieval_usingCQLStatements() {
        UUID uuid = UUIDs.timeBased();
        String insertCql = "insert into book (id, title, publisher, tags) values " + "(" + uuid + ", 'Head First Java', 'OReilly Media', {'Software'})";
        cassandraOperations.execute(insertCql);
        Select select = QueryBuilder.select().from("book").limit(10);
        Book actualBook = cassandraOperations.selectOne(select, Book.class);
        assertEquals(uuid, actualBook.getId());
    }

    @Test
    public void whenSavingBook_thenAvailableOnRetrieval_usingPreparedStatements() throws InterruptedException {
        UUID uuid = UUIDs.timeBased();
        String insertPreparedCql = "insert into book (id, title, publisher, tags) values (?, ?, ?, ?)";
        List<Object> singleBookArgsList = new ArrayList<>();
        List<List<?>> bookList = new ArrayList<>();
        singleBookArgsList.add(uuid);
        singleBookArgsList.add("Head First Java");
        singleBookArgsList.add("OReilly Media");
        singleBookArgsList.add(ImmutableSet.of("Software"));
        bookList.add(singleBookArgsList);
        cassandraOperations.ingest(insertPreparedCql, bookList);
        Select select = QueryBuilder.select().from("book");
        Book actualBook = cassandraOperations.selectOne(select, Book.class);
        assertEquals(uuid, actualBook.getId());
    }
}
