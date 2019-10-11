package demo.springdata.couchbase;

import com.couchbase.client.java.query.N1qlQuery;
import demo.springdata.couchbase.model.Airline;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.couchbase.config.BeanNames;
import org.springframework.data.couchbase.core.CouchbaseOperations;
import org.springframework.data.couchbase.repository.support.IndexManager;

import javax.annotation.PostConstruct;
import java.util.List;

@SpringBootApplication
@RequiredArgsConstructor
public class CouchbaseConfiguration {

    private final ObjectProvider<CouchbaseOperations> couchbaseOperationsProvider;

    @Bean(name = BeanNames.COUCHBASE_INDEX_MANAGER)
    public IndexManager indexManager() {
        return new IndexManager(true, true, false);
    }

    @PostConstruct
    private void postConstruct() {
        CouchbaseOperations couchbaseOperations = couchbaseOperationsProvider.getIfUnique();
        List<Airline> airlinesWithoutClassAttribute = couchbaseOperations.findByN1QL(N1qlQuery.simple(
                "SELECT META(`travel-sample`).id AS _ID, META(`travel-sample`).cas AS _CAS, `travel-sample`.* " +
                        "FROM `travel-sample` " +
                        "WHERE type = \"airline\" AND _class IS MISSING;"),
                Airline.class);

        airlinesWithoutClassAttribute.forEach(couchbaseOperations::save);
    }
}
