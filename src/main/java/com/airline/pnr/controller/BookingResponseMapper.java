package com.airline.pnr.controller;

import com.airline.pnr.model.Booking;
import com.airline.pnr.model.Flight;
import com.airline.pnr.model.Passenger;
import com.airline.pnr.openapi.model.BaggageResponse;
import com.airline.pnr.openapi.model.BookingResponse;
import com.airline.pnr.openapi.model.FlightResponse;
import com.airline.pnr.openapi.model.PassengerResponse;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Converts the Domain Model (Booking) to the API DTO (BookingResponse).
 */

@Component
public class BookingResponseMapper {
    public BookingResponse toResponse(Booking booking) {
        BookingResponse response = new BookingResponse();
        response.setPnr(booking.bookingReference().value());
        response.setCabinClass(booking.cabinClass());
        if (!booking.flights().isEmpty()) {
            response.setFlights(booking.flights().stream()
                                        .map(this::mapFlight)
                                        .toList());
        }
        if (booking.passengers() != null) {
            response.setPassengers(booking.passengers().stream()
                                          .map(this::mapPassenger)
                                          .toList());
        }
        if (booking.passengers() != null) {
            response.setPassengers(booking.passengers().stream()
                                          .map(this::mapPassenger)
                                          .toList());
        }
        return response;
    }
    
    private PassengerResponse mapPassenger(Passenger pax) {
        PassengerResponse passengerResponse = new PassengerResponse();
        passengerResponse.setPassengerNumber(pax.passengerNumber());
        passengerResponse.setFullName(buildFullName(pax));
        passengerResponse.setSeat(pax.seat());
        
        passengerResponse.setTicketUrl(pax.ticketUrl());
        passengerResponse.setCustomerId(pax.customerId() != null ? pax.customerId().value() : null);
        
        if (pax.baggage() != null) {
            passengerResponse.setBaggage(new BaggageResponse().allowanceUnit(pax.baggage().allowanceUnit()).checkedAllowanceValue(pax.baggage().checkedAllowanceValue()).carryOnAllowanceValue(pax.baggage().carryOnAllowanceValue()));
        }
        return passengerResponse;
    }
    
    private String buildFullName(Passenger p) {
        return Stream.of(p.firstName(), p.middleName(), p.lastName()).filter(s -> s != null && !s.isBlank()).collect(Collectors.joining(" "));
    }
    
    private FlightResponse mapFlight(Flight flight) {
        FlightResponse flightResponse = new FlightResponse();
        flightResponse.setFlightNumber(flight.flightNumber());
        flightResponse.setDepartureAirport(flight.departureAirport());
        flightResponse.setArrivalAirport(flight.arrivalAirport());
        
        if (flight.departureTimeStamp() != null) {
            flightResponse.setDepartureTimeStamp(flight.departureTimeStamp().atOffset(ZoneOffset.UTC));
        }
        if (flight.arrivalTimeStamp() != null) {
            flightResponse.setArrivalTimeStamp(flight.arrivalTimeStamp().atOffset(ZoneOffset.UTC));
        }
        return flightResponse;
    }
    
}