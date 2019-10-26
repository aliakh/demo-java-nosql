package demo.spring.data.cassandra.repository;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Statement;
import demo.spring.data.cassandra.AbstractIntegrationTest;
import org.apache.cassandra.exceptions.ConfigurationException;
import org.apache.thrift.transport.TTransportException;
import demo.spring.data.cassandra.config.CassandraConfig;
import demo.spring.data.cassandra.model.Book;
import org.hamcrest.core.Is;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cassandra.core.CqlOperations;
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
public class CqlTemplateIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private CqlOperations cqlTemplate;

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
    public void allowsExecutingCqlStatements() {
        insertBookUsingCqlString();
        insertBookUsingStatementBuildWithQueryBuilder();
        insertBookUsingPreparedStatement();

        ResultSet resultSet1 = cqlTemplate.query("select * from book where title='" + TITLE1 + "' and publisher='" + PUBLISHER + "'");

        assertThat(resultSet1.all().size(), Is.is(2));

        Select select = QueryBuilder.select().from("book")
                .where(QueryBuilder.eq("title", TITLE1))
                .and(QueryBuilder.eq("publisher", PUBLISHER)).limit(10);

        ResultSet resultSet2 = cqlTemplate.query(select);

        assertThat(resultSet2.all().size(), is(1));
    }

    private void insertBookUsingCqlString() {
        cqlTemplate.execute("insert into book (id, title, publisher, tags) " +
                "values (" + UUIDs.timeBased() + ", '" + TITLE1 + "', '" + PUBLISHER + "', {'" + TAG1 + "', '" + TAG1 + "'})");
    }

    private void insertBookUsingStatementBuildWithQueryBuilder() {
        Insert insertStatement = QueryBuilder.insertInto("book")
                .value("id", UUIDs.timeBased())
                .value("title", TITLE1)
                .value("publisher", PUBLISHER)
                .value("tags", ImmutableSet.of(TAG1, TAG2));
        cqlTemplate.execute(insertStatement);
    }

    private void insertBookUsingPreparedStatement() {
        PreparedStatement preparedStatement = cqlTemplate.getSession().prepare("insert into book (id, title, publisher, tags) values (?, ?, ?, ?)");
        Statement insertStatement = preparedStatement.bind(UUIDs.timeBased(), TITLE1, PUBLISHER, ImmutableSet.of(TAG1, TAG2));
        cqlTemplate.execute(insertStatement);
    }
}
