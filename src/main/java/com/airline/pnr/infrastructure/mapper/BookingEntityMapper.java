package com.airline.pnr.infrastructure.mapper;

import com.airline.pnr.domain.valueobjects.CustomerId;
import com.airline.pnr.domain.valueobjects.Pnr;
import com.airline.pnr.infrastructure.entities.BookingEntity;
import com.airline.pnr.model.Booking;
import com.airline.pnr.model.Flight;
import com.airline.pnr.model.Passenger;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookingEntityMapper {
    /**
     * Converts the raw Database Object (DBO) into the clean Read Model.
     * Note: ticketUrl and baggage are initialized as null here;
     * they are populated later by the Service using .withDetails().
     */
    public Booking toReadModel(BookingEntity dbo) {
        
        return new Booking(
                new Pnr(dbo.bookingReference()),
                dbo.cabinClass(),
                mapPassengers(dbo.passengers()),
                mapFlights(dbo.flights())
        );
    }
    
    private List<Passenger> mapPassengers(List<BookingEntity.PassengerDbo> passengerDbos) {
        if (passengerDbos == null) return List.of();
        
        return passengerDbos.stream()
                            .map(p -> new Passenger(
                                    p.passengerNumber(),
                                    p.firstName(),
                                    p.middleName(),
                                    p.lastName(),
                                    CustomerId.ofNullable(p.customerId()),
                                    p.seat(),
                                    null,
                                    null
                            ))
                            .collect(Collectors.toList());
    }
    
    private List<Flight> mapFlights(List<BookingEntity.FlightDbo> flightDbos) {
        if (flightDbos == null) return List.of();
        
        return flightDbos.stream()
                         .map(f -> new Flight(
                                 f.flightNumber(),
                                 f.departureAirport(),
                                 f.departureTimeStamp(),
                                 f.arrivalAirport(),
                                 f.arrivalTimeStamp()
                         ))
                         .collect(Collectors.toList());
    }
}