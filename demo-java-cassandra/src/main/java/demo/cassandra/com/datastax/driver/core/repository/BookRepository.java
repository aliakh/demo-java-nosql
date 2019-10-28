package demo.cassandra.com.datastax.driver.core.repository;

import java.util.ArrayList;
import java.util.List;

import demo.cassandra.com.datastax.driver.core.domain.Book;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

public class BookRepository {

    private final Session session;

    public BookRepository(Session session) {
        this.session = session;
    }

    public void createTable() {
        session.execute("CREATE TABLE IF NOT EXISTS " + "books" + "(" + "id uuid PRIMARY KEY, " + "title text," + "author text," + "subject text);");
    }

    public void createTableBooksByTitle() {
        session.execute("CREATE TABLE IF NOT EXISTS " + "books" + "ByTitle" + "(" + "id uuid, " + "title text," + "PRIMARY KEY (title, id));");
    }

    public void alterTablebooks(String columnName, String columnType) {
        session.execute("ALTER TABLE " + "books" + " ADD " + columnName + " " + columnType + ";");
    }

    public void insertBook(Book book) {
        session.execute("INSERT INTO " + "books" + "(id, title, author, subject) " + "VALUES (" + book.getId() + ", '" + book.getTitle() + "', '" + book.getAuthor() + "', '" + book.getSubject() + "');");
    }

    public void insertBookByTitle(Book book) {
        session.execute("INSERT INTO " + "books" + "ByTitle" + "(id, title) " + "VALUES (" + book.getId() + ", '" + book.getTitle() + "');");
    }

    public void insertBookBatch(Book book) {
        String sb = "BEGIN BATCH " + "INSERT INTO " + "books" + "(id, title, author, subject) " + "VALUES (" + book.getId() + ", '" + book.getTitle() + "', '" + book.getAuthor() +
                "', '" + book.getSubject() + "');" + "INSERT INTO " + "books" + "ByTitle" + "(id, title) " + "VALUES (" + book.getId() + ", '" + book.getTitle() + "');" +
                "APPLY BATCH;";
        session.execute(sb);
    }

    public Book selectByTitle(String title) {
        ResultSet rs = session.execute("SELECT * FROM " + "books" + "ByTitle" + " WHERE title = '" + title + "';");

        List<Book> books = new ArrayList<Book>();
        for (Row r : rs) {
            Book s = new Book(r.getUUID("id"), r.getString("title"), null, null);
            books.add(s);
        }

        return books.get(0);
    }

    public List<Book> selectAll() {
        ResultSet rs = session.execute("SELECT * FROM " + "books");

        List<Book> books = new ArrayList<Book>();
        for (Row r : rs) {
            Book book = new Book(r.getUUID("id"), r.getString("title"), r.getString("author"), r.getString("subject"));
            books.add(book);
        }
        return books;
    }

    public List<Book> selectAllBookByTitle() {
        ResultSet rs = session.execute("SELECT * FROM " + "books" + "ByTitle");

        List<Book> books = new ArrayList<Book>();
        for (Row r : rs) {
            Book book = new Book(r.getUUID("id"), r.getString("title"), null, null);
            books.add(book);
        }
        return books;
    }

    public void deleteBookByTitle(String title) {
        session.execute("DELETE FROM " + "books" + "ByTitle" + " WHERE title = '" + title + "';");
    }

    public void deleteTable(String tableName) {
        session.execute("DROP TABLE IF EXISTS " + tableName);
    }
}
