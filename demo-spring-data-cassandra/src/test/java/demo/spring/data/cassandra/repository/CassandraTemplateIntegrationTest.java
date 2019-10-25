package demo.spring.data.cassandra.repository;

import static junit.framework.TestCase.assertNull;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.cassandra.exceptions.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.thrift.transport.TTransportException;
import demo.spring.data.cassandra.config.CassandraConfig;
import demo.spring.data.cassandra.model.Book;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cassandra.core.cql.CqlIdentifier;
import org.springframework.data.cassandra.core.CassandraAdminOperations;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
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
    public static void startEmbeddedCassandra() throws InterruptedException, TTransportException, ConfigurationException, IOException {
        AbstractIntegrationTest.startEmbeddedCassandra();
    }

    @AfterClass
    public static void stopEmbeddedCassandra() {
        AbstractIntegrationTest.stopEmbeddedCassandra();
    }

    @Before
    public void createTable() {
        super.createTable();
    }

    @After
    public void dropTable() {
        super.dropTable();
    }

    @Test
    public void whenSavingBook_thenAvailableOnRetrieval() {
        Book book1 = new Book(UUIDs.timeBased(), "Head First Java", "O'Reilly Media", ImmutableSet.of("Computer", "Software"));
        cassandraTemplate.insert(book1);
        Select select = QueryBuilder.select().from("book").where(QueryBuilder.eq("title", "Head First Java")).and(QueryBuilder.eq("publisher", "O'Reilly Media")).limit(10);
        Book retrievedBook = cassandraTemplate.selectOne(select, Book.class);
        assertEquals(book1.getId(), retrievedBook.getId());
    }

    @Test
    public void whenSavingBooks_thenAllAvailableOnRetrieval() {
        Book book1 = new Book(UUIDs.timeBased(), "Head First Java", "O'Reilly Media", ImmutableSet.of("Computer", "Software"));
        Book book2 = new Book(UUIDs.timeBased(), "Head Design Patterns", "O'Reilly Media", ImmutableSet.of("Computer", "Software"));
        final List<Book> bookList = new ArrayList<>();
        bookList.add(book1);
        bookList.add(book2);
        cassandraTemplate.insert(bookList);

        Select select = QueryBuilder.select().from("book").limit(10);
        final List<Book> retrievedBooks = cassandraTemplate.select(select, Book.class);
        assertThat(retrievedBooks.size(), is(2));
        assertEquals(book1.getId(), retrievedBooks.get(0).getId());
        assertEquals(book2.getId(), retrievedBooks.get(1).getId());
    }

    @Test
    public void whenUpdatingBook_thenShouldUpdatedOnRetrieval() {
        Book book1 = new Book(UUIDs.timeBased(), "Head First Java", "O'Reilly Media", ImmutableSet.of("Computer", "Software"));
        cassandraTemplate.insert(book1);
        Select select = QueryBuilder.select().from("book").limit(10);
        Book retrievedBook = cassandraTemplate.selectOne(select, Book.class);
        retrievedBook.setTags(ImmutableSet.of("Java", "Programming"));
        cassandraTemplate.update(retrievedBook);
        Book retrievedUpdatedBook = cassandraTemplate.selectOne(select, Book.class);
        assertEquals(retrievedBook.getTags(), retrievedUpdatedBook.getTags());
    }

    @Test
    public void whenDeletingASelectedBook_thenNotAvailableOnRetrieval() throws InterruptedException {
        Book book1 = new Book(UUIDs.timeBased(), "Head First Java", "OReilly Media", ImmutableSet.of("Computer", "Software"));
        cassandraTemplate.insert(book1);
        cassandraTemplate.delete(book1);
        Select select = QueryBuilder.select().from("book").limit(10);
        Book retrievedUpdatedBook = cassandraTemplate.selectOne(select, Book.class);
        assertNull(retrievedUpdatedBook);
    }

    @Test
    public void whenDeletingAllBooks_thenNotAvailableOnRetrieval() {
        Book book1 = new Book(UUIDs.timeBased(), "Head First Java", "O'Reilly Media", ImmutableSet.of("Computer", "Software"));
        Book book2 = new Book(UUIDs.timeBased(), "Head Design Patterns", "O'Reilly Media", ImmutableSet.of("Computer", "Software"));
        cassandraTemplate.insert(book1);
        cassandraTemplate.insert(book2);
        cassandraTemplate.deleteAll(Book.class);
        Select select = QueryBuilder.select().from("book").limit(10);
        Book retrievedUpdatedBook = cassandraTemplate.selectOne(select, Book.class);
        assertNull(retrievedUpdatedBook);
    }

    @Test
    public void whenAddingBooks_thenCountShouldBeCorrectOnRetrieval() {
        Book book1 = new Book(UUIDs.timeBased(), "Head First Java", "O'Reilly Media", ImmutableSet.of("Computer", "Software"));
        Book book2 = new Book(UUIDs.timeBased(), "Head Design Patterns", "O'Reilly Media", ImmutableSet.of("Computer", "Software"));
        cassandraTemplate.insert(book1);
        cassandraTemplate.insert(book2);
        final long bookCount = cassandraTemplate.count(Book.class);
        assertEquals(2, bookCount);
    }
}
