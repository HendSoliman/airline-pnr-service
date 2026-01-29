package com.airline.pnr.controller;

import com.airline.pnr.Fixture.BookingFixture;
import com.airline.pnr.model.Booking;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.when;


@WebFluxTest(controllers = PnrQueryController.class) // Narrow the focus here
@DisplayName("PNR Query Controller Tests")
@Import({GlobalErrorHandler.class, BookingResponseMapper.class})
class PnrQueryControllerTest {
    
    @Autowired
    private WebTestClient webTestClient;
    
    @MockitoBean
    private BookingQueryAdapter bookingQueryAdapter;

    
    @Test
    @DisplayName("should return booking when PNR data")
    void shouldReturnBookingWhenPnrExists() {
        // Arrange
        String pnr = "GHTW42";
        
        // 1. Create a Domain Fixture based on your provided Booking.java
        Booking mockBooking = BookingFixture.ofPnr(pnr);
        
        // Stubbing the reactive flow
        when(bookingQueryAdapter.execute(pnr)).thenReturn(Mono.just(mockBooking));
        
        // Act & Assert: Execute the request and verify the result
        webTestClient.get()
                     .uri("/booking/{pnr}", pnr)
                     .exchange()
                     .expectStatus().isOk()
                     .expectBody()
                     .jsonPath("$.pnr").isEqualTo(pnr);
    }
}