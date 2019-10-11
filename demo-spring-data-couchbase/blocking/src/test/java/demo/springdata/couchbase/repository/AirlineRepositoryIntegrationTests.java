package demo.springdata.couchbase.repository;

import demo.springdata.couchbase.model.Airline;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.couchbase.core.CouchbaseOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AirlineRepositoryIntegrationTests {

    @Autowired
    AirlineRepository airlineRepository;

    @Autowired
    CouchbaseOperations couchbaseOperations;

    @Before
    public void before() {
        airlineRepository.findById("LH").ifPresent(couchbaseOperations::remove);
    }

    @Test
    public void shouldFindAirlineN1ql() {
        Airline airline = airlineRepository.findAirlineByIataCode("TQ");

        assertThat(airline.getCallsign()).isEqualTo("TXW");
    }

    @Test
    public void shouldFindById() {
        Airline airline = airlineRepository.findAirlineByIataCode("TQ");

        assertThat(airlineRepository.findById(airline.getId())).contains(airline);
        assertThat(airlineRepository.findById("unknown")).isEmpty();
    }

    @Test
    public void shouldFindByView() {
        List<Airline> airlines = airlineRepository.findAllBy();

        assertThat(airlines).hasSize(187);
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

        airlineRepository.save(airline);

        assertThat(airlineRepository.findById("LH")).contains(airline);
    }
}
