package com.airline.pnr.infrastructure.mappers;


import com.airline.pnr.domain.valueobjects.CustomerId;
import com.airline.pnr.domain.valueobjects.Pnr;
import com.airline.pnr.infrastructure.entities.BookingEntity;
import com.airline.pnr.model.Booking;
import com.airline.pnr.model.Flight;
import com.airline.pnr.model.Passenger;
import org.springframework.stereotype.Component;


@Component
public class BookingMapper {
    /**
     * Converts the raw Database Object (DBO) into the clean Read Model.
     * Note: ticketUrl and baggage are initialized as null here;
     * they are populated later by the other services.
     */
    public Booking toReadModel(BookingEntity entity) {
        return new Booking(
                new Pnr(entity.bookingReference()),
                entity.cabinClass(),
                entity.passengers().stream()
                      .map(BookingMapper::mapPassenger)
                      .toList(),
                entity.flights().stream()
                      .map(BookingMapper::mapFlight)
                      .toList(),
                entity.updatedAt()
        );
    }
    
    private static Flight mapFlight(BookingEntity.FlightDbo dbo) {
        return new Flight(
                dbo.flightNumber(),
                dbo.departureAirport(),
                dbo.departureTimeStamp(),
                dbo.arrivalAirport(),
                dbo.arrivalTimeStamp()
        );
    }
    
    private static Passenger mapPassenger(BookingEntity.PassengerDbo passengerDbo) {
        return new Passenger(
                passengerDbo.passengerNumber(),
                passengerDbo.firstName(),
                passengerDbo.middleName(),
                passengerDbo.lastName(),
                passengerDbo.customerId() == null ? null : new CustomerId(passengerDbo.customerId().get()),
                passengerDbo.seat(),
                null, // ticketUrl populated later
                null  // baggage  populated later
        );
    }
}