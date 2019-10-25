package demo.spring.data.cassandra.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.IOException;
import java.util.HashMap;

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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.utils.UUIDs;
import com.google.common.collect.ImmutableSet;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CassandraConfig.class)
public class BookRepositoryIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private BookRepository bookRepository;

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
        Book book = new Book(UUIDs.timeBased(), "Head First Java", "O'Reilly Media", ImmutableSet.of("Computer", "Software"));
        bookRepository.save(book);
        Iterable<Book> actualBooks = bookRepository.findByTitleAndPublisher("Head First Java", "O'Reilly Media");
        assertEquals(book.getId(), actualBooks.iterator().next().getId());
    }

    @Test
    public void whenUpdatingBooks_thenAvailableOnRetrieval() {
        Book book1 = new Book(UUIDs.timeBased(), "Head First Java", "O'Reilly Media", ImmutableSet.of("Computer", "Software"));
        bookRepository.save(book1);
        Iterable<Book> books = bookRepository.findByTitleAndPublisher("Head First Java", "O'Reilly Media");
        book1.setTitle("Head First Java Second Edition");
        bookRepository.save(book1);
        Iterable<Book> updateBooks = bookRepository.findByTitleAndPublisher("Head First Java Second Edition", "O'Reilly Media");
        assertEquals(book1.getTitle(), updateBooks.iterator().next().getTitle());
    }

    @Test(expected = java.util.NoSuchElementException.class)
    public void whenDeletingExistingBooks_thenNotAvailableOnRetrieval() {
        Book book = new Book(UUIDs.timeBased(), "Head First Java", "O'Reilly Media", ImmutableSet.of("Computer", "Software"));
        bookRepository.save(book);
        bookRepository.delete(book);
        Iterable<Book> actualBook = bookRepository.findByTitleAndPublisher("Head First Java", "O'Reilly Media");
        assertNotEquals(book.getId(), actualBook.iterator().next().getId());
    }

    @Test
    public void whenSavingBooks_thenAllShouldAvailableOnRetrieval() {
        Book book1 = new Book(UUIDs.timeBased(), "Head First Java", "O'Reilly Media", ImmutableSet.of("Computer", "Software"));
        Book book2 = new Book(UUIDs.timeBased(), "Head Design Patterns", "O'Reilly Media", ImmutableSet.of("Computer", "Software"));
        bookRepository.save(book1);
        bookRepository.save(book2);
        Iterable<Book> actualBooks = bookRepository.findAll();
        int bookCount = 0;
        for (Book book : actualBooks) {
            bookCount++;
        }
        assertEquals(bookCount, 2);
    }
}
