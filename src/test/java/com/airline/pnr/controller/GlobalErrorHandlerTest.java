package com.airline.pnr.controller;

import com.airline.pnr.domain.exception.BookingNotFoundException;
import com.airline.pnr.infrastructure.BookingRepositoryImpl;
import com.airline.pnr.openapi.model.ProblemDetails;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers =PnrQueryController.class)
@Import(GlobalErrorHandler.class)
class GlobalErrorHandlerTest {
    
    @Autowired
    private WebTestClient webTestClient;
    
    @MockitoBean
    private BookingQueryAdapter bookingQueryAdapter;
    
    @MockitoBean
    private BookingResponseMapper bookingResponseMapper;
    
    String pnr = "GHTW42";
    
    @Test
    @DisplayName("404: NoSuchElementException ")
    void shouldHandleNotFound() {
        String pnr = "MISSING";
        when(bookingQueryAdapter.execute(pnr)).thenReturn(Mono.error(new BookingNotFoundException(pnr)));
        
        webTestClient.get()
                     .uri("/booking/{pnr}", pnr)
                     .exchange()
                     .expectStatus().isNotFound()
                     .expectBody(ProblemDetails.class)
                     .consumeWith(result -> {
                         var actual = result.getResponseBody();
                         var expected = new ProblemDetails()
                                 .status(404)
                                 .title("Booking Not Found").detail("No booking exists for the provided reference: MISSING")
                                 .instance("/booking/" + pnr)
                                 .type("about:blank");
                         
                         assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
                     });
    }
    
    @Test
    @DisplayName("500: Unexpected error")
    void shouldHandleUnexpectedError() {
        
        String errorMsg = "Database connection timed out";
        when(bookingQueryAdapter.execute(pnr)).thenReturn(Mono.error(new RuntimeException(errorMsg)));
        
        webTestClient.get()
                     .uri("/booking/{pnr}", pnr)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody(ProblemDetails.class)
                     .consumeWith(result -> {
                         var actual = result.getResponseBody();
                         assertThat(actual.getTitle()).isEqualTo("Internal Server Error");
                         assertThat(actual.getDetail()).isEqualTo(errorMsg);
                         assertThat(actual.getStatus()).isEqualTo(500);
                     });
    }
    
    @Test
    @DisplayName("404: Invalid path")
    void shouldHandleInvalidPath() {
        // Attempting to hit an endpoint that does not exist
        webTestClient.get()
                     .uri("/booking/nnd/qsiwdi")
                     .exchange()
                     .expectStatus().isNotFound()
                     .expectBody(ProblemDetails.class)
                     .consumeWith(result -> {
                         var actual = result.getResponseBody();
                         
                         assertThat(actual).isNotNull();
                         assertThat(actual.getStatus()).isEqualTo(404);
                         assertThat(actual.getTitle()).isEqualTo("Not Found");
                         assertThat(actual.getInstance()).isEqualTo("/booking/nnd/qsiwdi");
                         assertThat(actual.getDetail()).contains("endpoint does not exist");
                     });
    }
    
}