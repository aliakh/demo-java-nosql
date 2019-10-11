/*
 * Copyright 2017-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package demo.springdata.couchbase.repository;

import com.couchbase.client.java.query.N1qlQuery;
import demo.springdata.couchbase.model.Airline;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.couchbase.config.BeanNames;
import org.springframework.data.couchbase.core.CouchbaseOperations;
import org.springframework.data.couchbase.repository.support.IndexManager;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Simple configuration class.
 *
 * @author Chandana Kithalagama
 * @author Mark Paluch
 */
@SpringBootApplication
@RequiredArgsConstructor
public class CouchbaseConfiguration {

    private final CouchbaseOperations couchbaseOperations;

    @Bean(name = BeanNames.COUCHBASE_INDEX_MANAGER)
    public IndexManager indexManager() {
        return new IndexManager(true, true, false);
    }

    @PostConstruct
    private void postConstruct() {
        List<Airline> airlinesWithoutClassAttribute = couchbaseOperations.findByN1QL(N1qlQuery.simple(
                "SELECT META(`travel-sample`).id AS _ID, META(`travel-sample`).cas AS _CAS, `travel-sample`.* " +
                        "FROM `travel-sample` " +
                        "WHERE type = \"airline\" AND _class IS MISSING;"),
                Airline.class);

        airlinesWithoutClassAttribute.forEach(couchbaseOperations::save);
    }
}
