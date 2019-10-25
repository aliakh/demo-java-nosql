package demo.spring.data.cassandra.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.cassandra.config.CassandraClusterFactoryBean;
import org.springframework.data.cassandra.config.java.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.mapping.BasicCassandraMappingContext;
import org.springframework.data.cassandra.mapping.CassandraMappingContext;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@Configuration
@PropertySource(value = {"classpath:cassandra.properties"})
@EnableCassandraRepositories(basePackages = "demo.spring.data.cassandra.repository")
public class CassandraConfig extends AbstractCassandraConfiguration {

    private static final Log LOGGER = LogFactory.getLog(CassandraConfig.class);

    @Autowired
    private Environment environment;

    @Override
    protected String getKeyspaceName() {
        return environment.getProperty("cassandra.keyspace");
    }

    @Override
    @Bean
    public CassandraClusterFactoryBean cluster() {
        String contactPoints = environment.getProperty("cassandra.contact.points");
        int port = Integer.parseInt(environment.getProperty("cassandra.port"));
        LOGGER.info(String.format("Cassandra cluster: contactPoints=%s, port=%d", contactPoints, port));
        CassandraClusterFactoryBean cluster = new CassandraClusterFactoryBean();
        cluster.setContactPoints(contactPoints);
        cluster.setPort(port);
        return cluster;
    }

    @Override
    @Bean
    public CassandraMappingContext cassandraMapping() {
        return new BasicCassandraMappingContext();
    }
}