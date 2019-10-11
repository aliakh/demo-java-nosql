package demo.springdata.couchbase.model;

import com.couchbase.client.java.repository.annotation.Field;
import com.couchbase.client.java.repository.annotation.Id;
import lombok.Data;
import org.springframework.data.couchbase.core.mapping.Document;

@Data
@Document
public class Airline {

    @Id
    private String id;

    @Field
    private String type;

    @Field
    private String name;

    @Field("iata")
    private String iataCode;

    @Field
    private String icao;

    @Field
    private String callsign;

    @Field
    private String country;
}
