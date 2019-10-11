package demo.springdata.couchbase.repository;

import demo.springdata.couchbase.model.Airline;
import org.springframework.data.couchbase.core.query.N1qlPrimaryIndexed;
import org.springframework.data.couchbase.core.query.View;
import org.springframework.data.couchbase.core.query.ViewIndexed;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@N1qlPrimaryIndexed
@ViewIndexed(designDoc = "airlines")
public interface AirlineRepository extends CrudRepository<Airline, String> {

    Airline findAirlineByIataCode(String code);

    @View(designDocument = "airlines", viewName = "all")
    List<Airline> findAllBy();
}
