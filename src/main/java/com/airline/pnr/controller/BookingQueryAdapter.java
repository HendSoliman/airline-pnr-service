package com.airline.pnr.controller;

import com.airline.pnr.model.Booking;
import com.airline.pnr.services.BookingAggregatorQueryService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class BookingQueryAdapter {
    
    private final BookingAggregatorQueryService service;
    
    public BookingQueryAdapter(BookingAggregatorQueryService service) {
        this.service = service;
    }
    
    public Mono<Booking> execute(String pnr) {
        return Mono.fromCompletionStage(
                service.execute(pnr).toCompletionStage()
        );
    }
}