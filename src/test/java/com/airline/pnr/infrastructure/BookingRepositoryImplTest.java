package com.airline.pnr.infrastructure;


import com.airline.pnr.infrastructure.access.ReactiveBookingRepository;
import com.airline.pnr.infrastructure.entities.BookingEntity;
import com.airline.pnr.model.Booking;
import io.vertx.core.Future;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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
    
    @InjectMocks
    private BookingRepositoryImpl BookingRepositoryImp;
    
    @Test
    @DisplayName(" Should return Future of   BookingAllowances for given passenger IDs and PNR")
    void should_return_future_of_bags(VertxTestContext testContext) {
        // Arrange
        String pnr = "GHTW42";

//        public record BookingEntity(
//                @Id String id,
//                @Indexed String bookingReference,
//                String cabinClass,
//                List<com.airline.pnr.infrastructure.entities.BookingEntity.PassengerDbo> passengers,
//                List<com.airline.pnr.infrastructure.entities.BookingEntity.FlightDbo> flights,
//                @CreatedDate Instant createdAt,
//                @LastModifiedDate Instant updatedAt
        
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
                BookingRepositoryImp.findByPnr(pnr);
        
        
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