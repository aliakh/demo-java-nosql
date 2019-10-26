package demo.spring.data.cassandra.repository;

import static junit.framework.TestCase.assertNull;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import demo.spring.data.cassandra.AbstractIntegrationTest;
import org.apache.cassandra.exceptions.ConfigurationException;
import org.apache.thrift.transport.TTransportException;
import demo.spring.data.cassandra.config.CassandraConfig;
import demo.spring.data.cassandra.model.Book;
import org.hamcrest.core.IsEqual;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.core.utils.UUIDs;
import com.google.common.collect.ImmutableSet;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CassandraConfig.class)
public class CassandraTemplateIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private CassandraOperations cassandraTemplate;

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
    public void supportsPojoToCqlMappings() {
        Book book = new Book(UUIDs.timeBased(), "Head First Java", "O'Reilly Media", ImmutableSet.of("Computer", "Software"));
        cassandraTemplate.insert(book);
        cassandraTemplate.insert(book);

        Select select = QueryBuilder.select().from(getTableName()).where(QueryBuilder.eq("title", "Head First Java")).and(QueryBuilder.eq("publisher", "O'Reilly Media")).limit(10);

        Book actualBook = cassandraTemplate.selectOne(select, Book.class);

        assertThat(actualBook, IsEqual.equalTo(book));

        List<Book> actualBooks = cassandraTemplate.select(select, Book.class);

        assertThat(actualBooks.size(), is(1));
        assertThat(actualBooks, hasItem(book));
    }
}
