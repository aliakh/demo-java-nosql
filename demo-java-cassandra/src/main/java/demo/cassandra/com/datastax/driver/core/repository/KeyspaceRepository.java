package demo.cassandra.com.datastax.driver.core.repository;

import com.datastax.driver.core.Session;

public class KeyspaceRepository {

    private Session session;

    public KeyspaceRepository(Session session) {
        this.session = session;
    }

    public void createKeyspace(String keyspaceName, String replicatioonStrategy, int numberOfReplicas) {
        session.execute("CREATE KEYSPACE IF NOT EXISTS " + keyspaceName + " WITH replication = {" + "'class':'" + replicatioonStrategy + "','replication_factor':" + numberOfReplicas + "};");
    }

    public void useKeyspace(String keyspace) {
        session.execute("USE " + keyspace);
    }

    public void deleteKeyspace(String keyspaceName) {
        session.execute("DROP KEYSPACE " + keyspaceName);
    }
}
