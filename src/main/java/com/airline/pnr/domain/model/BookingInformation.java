package com.airline.pnr.domain.model;

import com.airline.pnr.domain.valueobjects.Pnr;

import java.time.Instant;
import java.util.List;

public record BookingInformation(
        Pnr bookingReference,
        String cabinClass,
        List<Passenger> passengers,
        List<Flight> flights,
        Instant updatedAt
)
{
}