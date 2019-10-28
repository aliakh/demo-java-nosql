package demo.cassandra.com.datastax.driver.core;

import com.datastax.driver.core.Session;
import com.datastax.driver.core.utils.UUIDs;
import demo.cassandra.com.datastax.driver.core.domain.Book;
import demo.cassandra.com.datastax.driver.core.repository.BookRepository;
import demo.cassandra.com.datastax.driver.core.repository.KeyspaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CassandraClient {

    private static final Logger LOG = LoggerFactory.getLogger(CassandraClient.class);

    public static void main(String[] args) {

        CassandraConnector connector = new CassandraConnector();
        connector.connect("127.0.0.1", null);
        Session session = connector.getSession();

        KeyspaceRepository keyspaceRepository = new KeyspaceRepository(session);
        keyspaceRepository.createKeyspace("library", "SimpleStrategy", 1);
        keyspaceRepository.useKeyspace("library");

        BookRepository bookRepository = new BookRepository(session);
        bookRepository.createTable();
        bookRepository.alterTablebooks("publisher", "text");

        bookRepository.createTableBooksByTitle();

        Book book = new Book(UUIDs.timeBased(), "Effective Java", "Joshua Bloch", "Programming");
        bookRepository.insertBookBatch(book);

        bookRepository.selectAll().forEach(b -> LOG.info("Title in books: " + b.getTitle()));
        bookRepository.selectAllBookByTitle().forEach(b -> LOG.info("Title in booksByTitle: " + b.getTitle()));

        bookRepository.deleteBookByTitle("Effective Java");
        bookRepository.deleteTable("books");
        bookRepository.deleteTable("booksByTitle");

        keyspaceRepository.deleteKeyspace("library");

        connector.close();
    }
}
