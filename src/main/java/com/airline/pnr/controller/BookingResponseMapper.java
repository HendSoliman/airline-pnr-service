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
 * Converts the Domain Model (BookingInformation) to the API DTO (BookingResponse).
 */

@Component
public class BookingResponseMapper {
    
    public BookingResponse toResponse(Booking model) {
        BookingResponse response = new BookingResponse();
        response.setPnr(model.bookingReference().value());
        response.setCabinClass(model.cabinClass());
        response.setPassengers(model.passengers().stream().map(this::mapPassenger).toList());
        response.setFlights(model.flights().stream().map(this::mapFlight).toList());
        return response;
    }
    
    private PassengerResponse mapPassenger(Passenger p) {
        PassengerResponse dto = new PassengerResponse();
        dto.setPassengerNumber(p.passengerNumber());
        dto.setFullName(buildFullName(p));
        dto.setSeat(p.seat());
        
        // Only set if not null - helps Jackson's NON_NULL filter
        if (p.ticketUrl() != null) dto.setTicketUrl(p.ticketUrl());
        if (p.customerId() != null) dto.setCustomerId(p.customerId().value());
        
        if (p.baggage() != null) {
            dto.setBaggage(new BaggageResponse().allowanceUnit(p.baggage().allowanceUnit()).checkedAllowanceValue(p.baggage().checkedAllowanceValue()).carryOnAllowanceValue(p.baggage().carryOnAllowanceValue()));
        }
        return dto;
    }
    
    private String buildFullName(Passenger p) {
        return Stream.of(p.firstName(), p.middleName(), p.lastName()).filter(s -> s != null && !s.isBlank()).collect(Collectors.joining(" "));
    }
    
    private FlightResponse mapFlight(Flight f) {
        FlightResponse fr = new FlightResponse();
        fr.setFlightNumber(f.flightNumber());
        fr.setDepartureAirport(f.departureAirport());
        fr.setArrivalAirport(f.arrivalAirport());
        
        if (f.departureTimeStamp() != null) {
            fr.setDepartureTimeStamp(OffsetDateTime.ofInstant(f.departureTimeStamp(), ZoneOffset.UTC));
        }
        if (f.arrivalTimeStamp() != null) {
            fr.setArrivalTimeStamp(OffsetDateTime.ofInstant(f.arrivalTimeStamp(), ZoneOffset.UTC));
        }
        return fr;
    }
}