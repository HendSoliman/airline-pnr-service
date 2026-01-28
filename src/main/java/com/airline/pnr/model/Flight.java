package com.airline.pnr.model;
import java.time.OffsetDateTime;

public record Flight(
        String flightNumber,
        String departureAirport,
        OffsetDateTime departureTimeStamp,
        String arrivalAirport,
        OffsetDateTime arrivalTimeStamp
) {}