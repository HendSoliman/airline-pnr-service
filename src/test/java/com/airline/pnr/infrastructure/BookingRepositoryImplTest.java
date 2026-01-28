package com.airline.pnr.infrastructure;


import com.airline.pnr.infrastructure.access.ReactiveBookingRepository;
import com.airline.pnr.infrastructure.entities.BookingEntity;
import com.airline.pnr.infrastructure.mappers.BookingMapper;
import com.airline.pnr.model.Booking;
import io.vertx.core.Future;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


@ExtendWith({MockitoExtension.class, VertxExtension.class})
class BookingRepositoryImplTest {
    
    @Mock
    private ReactiveBookingRepository repo;
    
    private final BookingMapper mapper = new BookingMapper();
    
    private BookingRepositoryImpl bookingRepositoryImp;
    
    @BeforeEach
    void setUp() {
        
        bookingRepositoryImp = new BookingRepositoryImpl(repo, mapper);
    }
    
    @Test
    @DisplayName(" Should return Future of   Booking  PNR")
    void should_return_future_of_booking(VertxTestContext testContext) {
        // Arrange
        String pnr = "GHTW42";
        
        BookingEntity.PassengerDbo passenger1 =
                new BookingEntity.PassengerDbo("John", java.util.Optional.empty(),
                        "Doe", 1, java.util.Optional.empty(), "12A");
        
        BookingEntity.FlightDbo flight =
                new BookingEntity.FlightDbo("FL123", "JFK",
                        Instant.now().atOffset(java.time.ZoneOffset.UTC),
                        "LAX",
                        Instant.now().plusSeconds(3600).atOffset(java.time.ZoneOffset.UTC)
                );
        
        BookingEntity entity1 =
                new BookingEntity("1", pnr, "Economy", List.of(passenger1),
                        List.of(flight), Instant.now(), Instant.now());
        
        
        when(repo.findByBookingReference(pnr))
                .thenReturn(Mono.just(entity1));
        
        // Act
        Future<Booking> bookingModel =
                bookingRepositoryImp.findByPnr(pnr);
        
        
        // Assert 
        bookingModel.onComplete(testContext.succeeding(booking -> {
            testContext.verify(() -> {
                
                // Verify mapping accuracy
                assertThat(booking.bookingReference().value()).isEqualTo(pnr);
                assertThat(booking.cabinClass()).isEqualTo("Economy");
                assertThat(booking.passengers().size()).isEqualTo(1);
                
                assertEquals(1, booking.flights().size());
                
                testContext.completeNow();
            });
        }));
    }
}