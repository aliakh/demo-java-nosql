package demo.n1ql;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"demo.n1ql"})
public class IntegrationTestConfig {

    @Bean
    public Cluster cluster() {
        CouchbaseEnvironment env = DefaultCouchbaseEnvironment.builder()
          .connectTimeout(60000)
          .build();
        return CouchbaseCluster.create(env, "127.0.0.1");
    }


}
