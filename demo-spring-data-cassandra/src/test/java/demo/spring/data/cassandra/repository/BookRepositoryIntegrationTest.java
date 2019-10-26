package demo.spring.data.cassandra.repository;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import demo.spring.data.cassandra.AbstractIntegrationTest;
import demo.spring.data.cassandra.config.CassandraConfig;
import demo.spring.data.cassandra.model.Book;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.datastax.driver.core.utils.UUIDs;
import com.google.common.collect.ImmutableSet;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CassandraConfig.class)
public class BookRepositoryIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private BookRepository bookRepository;

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
    public void repositoryStoresAndRetrievesBooks() {
        Book book1 = new Book(UUIDs.timeBased(), TITLE1, PUBLISHER, ImmutableSet.of(TAG1, TAG2));
        Book book2 = new Book(UUIDs.timeBased(), TITLE2, PUBLISHER, ImmutableSet.of(TAG1, TAG2));
        bookRepository.saveAll(ImmutableSet.of(book1, book2));

        Iterable<Book> actualBooks = bookRepository.findByTitleAndPublisher(TITLE1, PUBLISHER);

        assertThat(actualBooks, hasItem(book1));
        assertThat(actualBooks, not(hasItem(book2)));
    }

    @Test
    public void repositoryDeletesStoredBooks() {
        Book book1 = new Book(UUIDs.timeBased(), TITLE1, PUBLISHER, ImmutableSet.of(TAG1, TAG2));
        Book book2 = new Book(UUIDs.timeBased(), TITLE2, PUBLISHER, ImmutableSet.of(TAG1, TAG2));
        bookRepository.saveAll(ImmutableSet.of(book1, book2));

        bookRepository.delete(book1);
        bookRepository.delete(book2);

        Iterable<Book> actualBooks = bookRepository.findByTitleAndPublisher(TITLE1, PUBLISHER);

        assertThat(actualBooks, not(hasItem(book1)));
        assertThat(actualBooks, not(hasItem(book2)));
    }
}
