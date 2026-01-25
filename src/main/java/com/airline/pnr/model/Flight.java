package com.airline.pnr.model;
import com.airline.pnr.config.MongoInstantDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Flight(
        String flightNumber,
        String departureAirport,
        
        @JsonDeserialize(using = MongoInstantDeserializer.class)
        Instant departureTimeStamp,
        String arrivalAirport,
        @JsonDeserialize(using = MongoInstantDeserializer.class)
        Instant arrivalTimeStamp
) {


}