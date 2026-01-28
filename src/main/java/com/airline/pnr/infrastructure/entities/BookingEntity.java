package com.airline.pnr.infrastructure.entities;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Document(collection ="bookings")
public record BookingEntity(
        @Id String id,
        @Indexed String bookingReference,
        String cabinClass,
        List<PassengerDbo> passengers,
        List<FlightDbo> flights,
        @CreatedDate Instant createdAt,
        @LastModifiedDate Instant updatedAt
)
{
    public record PassengerDbo(String firstName, Optional<String> middleName,
                               String lastName, int passengerNumber, Optional<String>  customerId,
                               String seat)
    {
    }
    
    public record FlightDbo(String flightNumber, String departureAirport, OffsetDateTime departureTimeStamp,
                            String arrivalAirport, OffsetDateTime arrivalTimeStamp)
    {
    }
}