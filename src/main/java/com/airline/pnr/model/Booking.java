package com.airline.pnr.model;

import com.airline.pnr.domain.valueobjects.Pnr;

import java.time.Instant;
import java.util.List;

public record Booking(
        Pnr bookingReference,
        String cabinClass,
        List<Passenger> passengers,
        List<Flight> flights,
        Instant updatedAt
)
{
}