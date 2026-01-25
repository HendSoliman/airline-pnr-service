package com.airline.pnr.controller;

import com.airline.pnr.application.BookingAggregatorQueryService;
import com.airline.pnr.openapi.model.BookingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class BookingController {
    
    @Autowired
    private BookingAggregatorQueryService service;
    
    @Autowired
    private PnrResponseMapper mapper;
    
    @GetMapping("/booking/{pnr}")
    public Mono<BookingResponse> getBooking(@PathVariable String pnr) {
        // Convert Vert.x Future to Project Reactor Mono
        return Mono.fromCompletionStage(
                service.execute(pnr).toCompletionStage()
        ).map(mapper::toResponse);
    }
}