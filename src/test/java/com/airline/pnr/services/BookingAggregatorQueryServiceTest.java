package com.airline.pnr.services;

import com.airline.pnr.Fixture.BookingFixture;
import com.airline.pnr.domain.exception.BookingNotFoundException;
import com.airline.pnr.domain.exception.PnrDomainException;
import com.airline.pnr.model.BaggageAllowance;
import com.airline.pnr.model.Booking;
import com.airline.pnr.services.contract.BaggageDomainRepo;
import com.airline.pnr.services.contract.BookingDomainRepo;
import com.airline.pnr.services.contract.EventPublisherPort;
import com.airline.pnr.services.contract.TicketDomainRepo;
import io.vertx.core.Future;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;


@ExtendWith({MockitoExtension.class, VertxExtension.class})
class BookingAggregatorQueryServiceTest {
    
    
    @Mock
    BookingDomainRepo bookingRepo;
    
    @Mock
    BaggageDomainRepo baggageRepo;
    
    @Mock
    TicketDomainRepo ticketRepo;
    
    @Mock
    EventPublisherPort eventPublisherPort;
    
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
        
        // Arrange
        when(bookingRepo.findByPnr("GHTW42"))
                .thenReturn(Future.succeededFuture(booking));
        
        when(baggageRepo.findBagsOfPassengers(List.of(1, 2), "GHTW42"))
                .thenReturn(Future.succeededFuture(List.of(
                        new BaggageAllowance(1,"kg", 30, 7),
                        new BaggageAllowance(2, "kg", 25, 5)
                )));
        
        when(ticketRepo.findTicketUrls(List.of(1, 2), "GHTW42"))
                .thenReturn(Future.succeededFuture(Map.of(
                        1, "ticket1",
                        2, "ticket2"
                )));
        
        // Act
        Future<Booking> result = service.execute("GHTW42");
        
        // Assert
        result.onComplete(testContext.succeeding(bokking -> testContext.verify(() -> {
            assertThat(bokking.passengers()).hasSize(2);
            assertThat(bokking.passengers().get(0).passengerNumber()).isEqualTo(1);
            assertThat(bokking.passengers().get(1).passengerNumber()).isEqualTo(2);
            
            assertThat(bokking.passengers().get(0).baggage().checkedAllowanceValue()).isEqualTo(30);
            assertThat(bokking.passengers().get(1).baggage().carryOnAllowanceValue()).isEqualTo(5);
            
            assertThat(bokking.passengers().get(1).ticketUrl().equals("ticket2"));
            
            testContext.completeNow();
        })));
    }
    
    @Test
    @DisplayName(" Should fail when booking not found ")
    void shouldFailWhenBookingNotFound(VertxTestContext testContext) {
        // Arrange
        when(bookingRepo.findByPnr("notFoundPnr"))
                .thenReturn(Future.failedFuture(new BookingNotFoundException("Booking not found")));
        
        // Act
        Future<Booking> result = service.execute("notFoundPnr");
        
        // Assert
        result.onComplete(testContext.failing(err -> testContext.verify(() -> {
            assertThat(err).isInstanceOf(PnrDomainException.class)
                           .hasMessageContaining("Booking not found");
            testContext.completeNow();
        })));
    }
    
    @Test
    @DisplayName(" Should handle empty passenger list ")
    void shouldHandleEmptyPassengerList(VertxTestContext testContext) {
//        TODO With business requirements clarified, implement this test case.
        testContext.completeNow();
    }
}