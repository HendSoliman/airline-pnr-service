package com.airline.pnr.controller;

import com.airline.pnr.domain.exception.PnrDomainException;
import com.airline.pnr.openapi.model.ProblemDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalErrorHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalErrorHandler.class);
    
    /**
     * Centralized exception handling for all controllers
     */
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ProblemDetails>> handleExceptionsGraceful(Exception ex, ServerHttpRequest request) {
        log.debug("Handling exception: {}", ex.getClass().getSimpleName());
        
        // We return a Mono to stay non-blocking
        return Mono.just(switch (ex) {
            case PnrDomainException d -> createResponse(d.getStatus(), d.getTitle(), d.getMessage(), request);
            case NoSuchElementException e -> createResponse(404, "Not Found", "Resource not found", request);
            // WebFlux specific validation exception
            case WebExchangeBindException e -> createResponse(400, "Bad Request", "Validation failed", request);
            case MethodNotAllowedException e -> createResponse(405, "Method Not Allowed",
                    "The method " + request.getMethod() + " is not supported for this endpoint", request);
            case ResponseStatusException e -> createResponse(e.getStatusCode().value(), "Not Found", "The requested endpoint does not exist", request);
            default -> {
                log.error("Unexpected error", ex);
                yield createResponse(500, "Internal Server Error", ex.getMessage(), request);
            }
        });
    }
    
    private ResponseEntity<ProblemDetails> createResponse(int status, String title, String detail, ServerHttpRequest request) {
        ProblemDetails problem = new ProblemDetails();
        problem.setStatus(status);
        problem.setTitle(title);
        problem.setDetail(detail);
        problem.setInstance(request.getPath().value());
        problem.setType("about:blank");
        
        return ResponseEntity.status(status).body(problem);
    }
}