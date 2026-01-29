package com.airline.pnr.model;
import java.time.Instant;

public record Flight(
        String flightNumber,
        String departureAirport,
        Instant departureTimeStamp,
        String arrivalAirport,
        Instant arrivalTimeStamp
) {}