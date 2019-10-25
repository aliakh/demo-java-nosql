package demo.spring.data.cassandra.repository;

import static junit.framework.TestCase.assertNull;
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
public class CassandraOperationsIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private CassandraOperations cassandraOperations;

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
        cassandraOperations.insert(book1);
        Select select = QueryBuilder.select().from("book").where(QueryBuilder.eq("title", "Head First Java")).and(QueryBuilder.eq("publisher", "O'Reilly Media")).limit(10);
        Book actualBook = cassandraOperations.selectOne(select, Book.class);
        assertEquals(book1.getId(), actualBook.getId());
    }

    @Test
    public void whenSavingBooks_thenAllAvailableOnRetrieval() {
        Book book1 = new Book(UUIDs.timeBased(), "Head First Java", "O'Reilly Media", ImmutableSet.of("Computer", "Software"));
        Book book2 = new Book(UUIDs.timeBased(), "Head Design Patterns", "O'Reilly Media", ImmutableSet.of("Computer", "Software"));
        List<Book> books = new ArrayList<>();
        books.add(book1);
        books.add(book2);
        cassandraOperations.insert(books);

        Select select = QueryBuilder.select().from("book").limit(10);
         List<Book> actualBooks = cassandraOperations.select(select, Book.class);
        assertThat(actualBooks.size(), is(2));
        assertEquals(book1.getId(), actualBooks.get(0).getId());
        assertEquals(book2.getId(), actualBooks.get(1).getId());
    }

    @Test
    public void whenUpdatingBook_thenShouldUpdatedOnRetrieval() {
        Book book1 = new Book(UUIDs.timeBased(), "Head First Java", "O'Reilly Media", ImmutableSet.of("Computer", "Software"));
        cassandraOperations.insert(book1);
        Select select = QueryBuilder.select().from("book").limit(10);
        Book actualBook = cassandraOperations.selectOne(select, Book.class);
        actualBook.setTags(ImmutableSet.of("Java", "Programming"));
        cassandraOperations.update(actualBook);
        Book retrievedUpdatedBook = cassandraOperations.selectOne(select, Book.class);
        assertEquals(actualBook.getTags(), retrievedUpdatedBook.getTags());
    }

    @Test
    public void whenDeletingASelectedBook_thenNotAvailableOnRetrieval() throws InterruptedException {
        Book book1 = new Book(UUIDs.timeBased(), "Head First Java", "OReilly Media", ImmutableSet.of("Computer", "Software"));
        cassandraOperations.insert(book1);
        cassandraOperations.delete(book1);
        Select select = QueryBuilder.select().from("book").limit(10);
        Book actualBook = cassandraOperations.selectOne(select, Book.class);
        assertNull(actualBook);
    }

    @Test
    public void whenDeletingAllBooks_thenNotAvailableOnRetrieval() {
        Book book1 = new Book(UUIDs.timeBased(), "Head First Java", "O'Reilly Media", ImmutableSet.of("Computer", "Software"));
        Book book2 = new Book(UUIDs.timeBased(), "Head Design Patterns", "O'Reilly Media", ImmutableSet.of("Computer", "Software"));
        cassandraOperations.insert(book1);
        cassandraOperations.insert(book2);
        cassandraOperations.truncate(Book.class);
        Select select = QueryBuilder.select().from("book").limit(10);
        Book actualBook = cassandraOperations.selectOne(select, Book.class);
        assertNull(actualBook);
    }

    @Test
    public void whenAddingBooks_thenCountShouldBeCorrectOnRetrieval() {
        Book book1 = new Book(UUIDs.timeBased(), "Head First Java", "O'Reilly Media", ImmutableSet.of("Computer", "Software"));
        Book book2 = new Book(UUIDs.timeBased(), "Head Design Patterns", "O'Reilly Media", ImmutableSet.of("Computer", "Software"));
        cassandraOperations.insert(book1);
        cassandraOperations.insert(book2);
        long actualBooksCount = cassandraOperations.count(Book.class);
        assertEquals(2, actualBooksCount);
    }
}
