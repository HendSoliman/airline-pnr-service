package com.airline.pnr.infrastructure;

import com.airline.pnr.GlobalErrorHandler;
import com.airline.pnr.domain.exception.BookingNotFoundException;
import com.airline.pnr.domain.exception.PnrDomainException;
import com.airline.pnr.openapi.model.ProblemDetails;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("Global Error Handler Tests")
class GlobalErrorHandlerTest {
    
    private GlobalErrorHandler errorHandler;
    private HttpServletRequest request;
    private static final String PNR = "GHTW42";
    
    @BeforeEach
    void setUp() {
        errorHandler = new GlobalErrorHandler();
        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/test-endpoint");
    }
    
    @Test
    void handlePnrDomainException_returnsCorrectResponse() {
        PnrDomainException ex = new BookingNotFoundException(PNR);
        
        var response = errorHandler.handleExceptionsGraceful(ex, request);
        
        ProblemDetails body = response.getBody();
        assertThat(response.getStatusCode().value()).isEqualTo(404);
        Assertions.assertNotNull(body.getTitle());
        assertThat(body.getInstance()).isEqualTo("/test-endpoint");
        assertThat(body.getType()).isEqualTo("about:blank");
    }
    
    @Test
    void handleNoResourceFoundException_returns404() {
        NoResourceFoundException ex = mock(NoResourceFoundException.class);
        
        var response = errorHandler.handleExceptionsGraceful(ex, request);
        
        ProblemDetails body = response.getBody();
        assertThat(response.getStatusCode().value()).isEqualTo(404);
               Assertions.assertNotNull(body.getTitle());
        assertThat(body.getInstance()).isEqualTo("/test-endpoint");
    }
    
    @Test
    void handleHttpRequestMethodNotSupportedException_returns405() {
        HttpRequestMethodNotSupportedException ex =
                new HttpRequestMethodNotSupportedException("POST");
        
        var response = errorHandler.handleExceptionsGraceful(ex, request);
        
        ProblemDetails body = response.getBody();
        assertThat(response.getStatusCode().value()).isEqualTo(405);
        assertThat(body.getTitle()).isEqualTo("Method Not Allowed");
        assertThat(body.getDetail()).contains("POST");
        assertThat(body.getInstance()).isEqualTo("/test-endpoint");
    }
    
    @Test
    void handleIllegalArgumentException_returns400() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid argument");
        
        var response = errorHandler.handleExceptionsGraceful(ex, request);
        
        ProblemDetails body = response.getBody();
        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(body.getTitle()).isEqualTo("Bad Request");
        assertThat(body.getDetail()).isEqualTo("Invalid argument");
        assertThat(body.getInstance()).isEqualTo("/test-endpoint");
    }
    
    @Test
    void handleNoSuchElementException_returns404() {
        NoSuchElementException ex = new NoSuchElementException();
        
        var response = errorHandler.handleExceptionsGraceful(ex, request);
        
        ProblemDetails body = response.getBody();
        assertThat(response.getStatusCode().value()).isEqualTo(404);
               Assertions.assertNotNull(body.getTitle());
        assertThat(body.getDetail()).isEqualTo("Resource not found");
        assertThat(body.getInstance()).isEqualTo("/test-endpoint");
    }
    
    @Test
    void handleUnknownException_returns500() {
        Exception ex = new Exception("Something went wrong");
        
        var response = errorHandler.handleExceptionsGraceful(ex, request);
        
        ProblemDetails body = response.getBody();
        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(body.getTitle()).isEqualTo("Internal Server Error");
        assertThat(body.getDetail()).isEqualTo("An unexpected error occurred");
        assertThat(body.getInstance()).isEqualTo("/test-endpoint");
    }
}
