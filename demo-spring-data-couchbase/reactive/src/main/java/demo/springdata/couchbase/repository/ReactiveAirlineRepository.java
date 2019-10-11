package demo.springdata.couchbase.repository;

import demo.springdata.couchbase.model.Airline;
import org.springframework.data.couchbase.core.query.N1qlPrimaryIndexed;
import org.springframework.data.couchbase.core.query.View;
import org.springframework.data.couchbase.core.query.ViewIndexed;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@N1qlPrimaryIndexed
@ViewIndexed(designDoc = "airlines")
public interface ReactiveAirlineRepository extends ReactiveCrudRepository<Airline, String> {

    Mono<Airline> findAirlineByIataCode(String code);

    @View(designDocument = "airlines", viewName = "all")
    Flux<Airline> findAllBy();
}
