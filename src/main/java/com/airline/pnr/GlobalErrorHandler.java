package com.airline.pnr;

import com.airline.pnr.domain.exception.PnrDomainException;
import com.airline.pnr.openapi.model.ProblemDetails;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalErrorHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalErrorHandler.class);
    /**
     * Centralized exception handling for all controllers
     */
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetails> handleExceptionsGraceful(Exception ex, HttpServletRequest request) {
        
        log.debug("Handling exception: {}", ex.getClass().getSimpleName(), ex);

        return switch (ex) {
            
            case PnrDomainException d -> createResponse(d.getStatus(), d.getTitle(), d.getMessage(), request); // Missing Resource
            case NoResourceFoundException e ->
                    createResponse(404, "Not Found", "Wrong API ", request);
            case HttpRequestMethodNotSupportedException e ->
                    createResponse(405, "Method Not Allowed", e.getMessage(), request);
            case IllegalArgumentException e -> createResponse(400, "Bad Request", e.getMessage(), request);
            case NoSuchElementException e -> createResponse(404, "Not Found", "Resource not found", request); //Optional.get()
            default -> createResponse(500, "Internal Server Error", "An unexpected error occurred", request);
        };
    }
    
    private ResponseEntity<ProblemDetails> createResponse(int status, String title, String detail, HttpServletRequest request) {
        ProblemDetails problem = new ProblemDetails();
        problem.setStatus(status);
        problem.setTitle(title);
        problem.setDetail(detail);
        problem.setInstance(request.getRequestURI());
        problem.setType("about:blank");
        
        return ResponseEntity.status(status).body(problem);
    }
    
}
