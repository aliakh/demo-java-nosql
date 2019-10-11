package demo.springdata.couchbase.template;

import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.view.ViewQuery;
import demo.springdata.couchbase.model.Airline;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.couchbase.core.RxJavaCouchbaseOperations;
import org.springframework.test.context.junit4.SpringRunner;
import rx.Observable;
import rx.observers.AssertableSubscriber;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RxJavaCouchbaseOperationsIntegrationTests {

    @Autowired
    RxJavaCouchbaseOperations operations;

    @Before
    public void before() {
        operations.findById("LH", Airline.class).flatMap(operations::remove).test().awaitTerminalEvent();
    }

    @Test
    public void shouldFindAirlineN1ql() {

        String n1ql = "SELECT META(`travel-sample`).id AS _ID, META(`travel-sample`).cas AS _CAS, `travel-sample`.* " + //
                "FROM `travel-sample` " + //
                "WHERE (`iata` = \"TQ\") AND `_class` = \"example.springdata.couchbase.model.Airline\"";

        AssertableSubscriber<Airline> subscriber = operations.findByN1QL(N1qlQuery.simple(n1ql), Airline.class) //
                .test() //
                .awaitTerminalEvent() //
                .assertCompleted();

        assertThat(subscriber.getOnNextEvents()).hasSize(1);
        assertThat(subscriber.getOnNextEvents().get(0).getCallsign()).isEqualTo("TXW");
    }

    @Test
    public void shouldFindByView() {

        Observable<Airline> airlines = operations.findByView(ViewQuery.from("airlines", "all"), Airline.class);

        airlines.test().awaitTerminalEvent().assertValueCount(187);
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

        Observable<Airline> single = operations.save(airline) //
                .map(Airline::getId) //
                .flatMap(id -> operations.findById(id, Airline.class));

        single.test().awaitTerminalEvent().assertResult(airline);
    }
}
