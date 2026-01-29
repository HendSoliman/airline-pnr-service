package com.airline.pnr.controller;


import com.airline.pnr.Fixture.BookingFixture;
import com.airline.pnr.model.Booking;
import com.airline.pnr.openapi.model.BookingResponse;
import com.airline.pnr.openapi.model.PassengerResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BookingResponseMapperTest {
    
    private BookingResponseMapper mapper;
    
    @BeforeEach
    void setUp() {
        mapper = new BookingResponseMapper();
    }
    
    @Test
    @DisplayName("Should map full booking fixture GHTW42 correctly")
    void shouldMapFullBooking() {
        // Arrange
        Booking domainBooking = BookingFixture.ofPnr("GHTW42");
        
        // Act
        BookingResponse response = mapper.toResponse(domainBooking);
        
        // Assert: General Booking Info
        assertThat(response).isNotNull();
        assertThat(response.getPnr()).isEqualTo("GHTW42");
        assertThat(response.getCabinClass()).isEqualTo("ECONOMY");
        
        // Assert: Passengers (James and Charles)
        assertThat(response.getPassengers()).hasSize(2);
        
        // Passenger 1: James ( null middle name + baggage)
        PassengerResponse james = response.getPassengers().getFirst();
        assertThat(james.getFullName()).isEqualTo("James McGill");
        assertThat(james.getBaggage()).isNotNull();
        assertThat(james.getBaggage().getCheckedAllowanceValue()).isEqualTo(30);
        assertThat(james.getTicketUrl()).isNull();
        
        // Passenger 2: Charles ( customerId + ticketUrl)
        PassengerResponse charles = response.getPassengers().get(1);
        assertThat(charles.getFullName()).isEqualTo("Charles McGill");
        assertThat(charles.getCustomerId()).isEqualTo("1216");
        assertThat(charles.getTicketUrl()).isEqualTo("emirates.com?ticket=someTicketRef");
        assertThat(charles.getBaggage()).isNull();
        
        // Flights
        assertThat(response.getFlights()).hasSize(1);
        var flight = response.getFlights().getFirst();
        assertThat(flight.getFlightNumber()).isEqualTo("EK231");
        assertThat(flight.getDepartureAirport()).isEqualTo("DXB");
        assertThat(flight.getArrivalAirport()).isEqualTo("IAD");
        assertThat(flight.getDepartureTimeStamp()).isNotNull();
        assertThat(flight.getArrivalTimeStamp()).isNotNull();
    }
    
    @Test
    @DisplayName("Should handle edge case where middle name is blank or null")
    void shouldHandleMissingNamesGracefully() {
        
        Booking domainBooking = BookingFixture.ofPnr("GHTW42");
        String fullName = mapper.toResponse(domainBooking).getPassengers().getFirst().getFullName();
        
        assertThat(fullName).doesNotContain("null");
        assertThat(fullName).isEqualTo("James McGill");
    }
}