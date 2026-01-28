package com.airline.pnr.services;

import com.airline.pnr.Fixture.BookingFixture;
import com.airline.pnr.model.Booking;
import com.airline.pnr.services.contract.BaggageDomainRepo;
import com.airline.pnr.services.contract.BookingDomainRepo;
import com.airline.pnr.services.contract.TicketDomainRepo;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith({MockitoExtension.class, VertxExtension.class})
class BookingAggregatorQueryServiceTest {
    
    
    @Mock
    BookingDomainRepo bookingRepo;
    
    @Mock
    BaggageDomainRepo baggageRepo;
    
    @Mock
    TicketDomainRepo ticketRepo;
    
    @InjectMocks
    BookingAggregatorQueryService service;
    
    private Booking booking;
    
    @BeforeEach
    void setup() {
        booking = BookingFixture.ofPnr("GHTW42");
        
    }
    
    @Test
    @DisplayName(" Should aggregate booking details successfully ")
    void shouldAggregateBookingDetailsSuccessfully(VertxTestContext testContext) {
    }
    
    @Test
    @DisplayName(" Should fail when booking not found ")
    void shouldFailWhenBookingNotFound(VertxTestContext testContext) {
    }
    
    @Test
    @DisplayName(" Should handle empty passenger list ")
    void shouldHandleEmptyPassengerList(VertxTestContext testContext) {
    }
}