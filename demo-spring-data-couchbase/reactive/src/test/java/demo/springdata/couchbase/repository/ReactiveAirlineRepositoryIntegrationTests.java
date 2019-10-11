package demo.springdata.couchbase.repository;

import demo.springdata.couchbase.model.Airline;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.couchbase.core.CouchbaseOperations;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

//import example.springdata.couchbase.util.CouchbaseAvailableRule;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ReactiveAirlineRepositoryIntegrationTests {

    @Autowired
    ReactiveAirlineRepository airlineRepository;

    @Autowired
    CouchbaseOperations couchbaseOperations;

    @Before
    public void before() {
        Airline toDelete = couchbaseOperations.findById("LH", Airline.class);

        if (toDelete != null) {
            couchbaseOperations.remove(toDelete);
        }
    }

    @Test
    public void shouldFindAirlineN1ql() {
        StepVerifier.create(airlineRepository.findAirlineByIataCode("TQ")).assertNext(it -> {

            assertThat(it.getCallsign()).isEqualTo("TXW");
        }).verifyComplete();
    }

    @Test
    public void shouldFindById() {
        Mono<Airline> airline = airlineRepository.findAirlineByIataCode("TQ")
                .map(Airline::getId)
                .flatMap(airlineRepository::findById);

        StepVerifier.create(airline).assertNext(it -> {

            assertThat(it.getCallsign()).isEqualTo("TXW");
        }).verifyComplete();

        StepVerifier.create(airlineRepository.findById("unknown")).verifyComplete();
    }

    @Test
    public void shouldFindByView() {
        StepVerifier.create(airlineRepository.findAllBy()).expectNextCount(187).verifyComplete();
    }

    @Test
    public void shouldCreateAirline() {
        Airline airline = new Airline();

        airline.setId("LH");
        airline.setIataCode("LH");
        airline.setIcao("DLH");
        airline.setCallsign("Lufthansa");
        airline.setName("Lufthansa");
        airline.setCountry("Germany");

        Mono<Airline> airlineMono = airlineRepository.save(airline)
                .map(Airline::getId)
                .flatMap(airlineRepository::findById);

        StepVerifier.create(airlineMono).expectNext(airline).verifyComplete();
    }
}
