package com.airline.pnr.infrastructure;

import com.airline.pnr.infrastructure.access.ReactiveTicketRepository;
import com.airline.pnr.infrastructure.entities.TicketEntity;
import io.vertx.core.Future;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;


@ExtendWith({MockitoExtension.class, VertxExtension.class})
class TicketRepositoryImplTest {
    
    
    @Mock
    private ReactiveTicketRepository repo;
    
    @InjectMocks
    private TicketRepositoryImpl ticketRepositoryImp;
    
    @Test
    @DisplayName(" Should return Future of ticket URLs for given passenger IDs and PNR")
    void should_return_future_of_available_ticket_urls(VertxTestContext testContext) {
        // Arrange
        String pnr = "GHTW42";
        List<Integer> passengerIds = List.of(1);


//        String id,
//        String bookingReference,
//        int passengerNumber,
//        Optional<String> ticketUrl
        
        TicketEntity ticket1 = new TicketEntity("ticketId1", pnr, 1, Optional.of("emirates.com?ticket=someTicketRef"));
        TicketEntity ticket2 = new TicketEntity("ticketId2", pnr, 2, null);
        
        when(repo.findByBookingReferenceAndPassengerNumberIn(pnr, passengerIds))
                .thenReturn(Flux.just(ticket1, ticket2));
        
        // Act
        Future<Map<Integer, String>> ticketUrls =
                ticketRepositoryImp.findTicketUrls(passengerIds, pnr);
        
        
        // Assert
        ticketUrls.onComplete(testContext.succeeding(map -> {
            testContext.verify(() -> {
                assertThat(map)
                        .hasSize(1)
                        .containsEntry(1, "emirates.com?ticket=someTicketRef");
                testContext.completeNow();
            });
        }));
        
        
    }
}