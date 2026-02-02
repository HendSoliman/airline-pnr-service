package com.airline.pnr.controller;

import com.airline.pnr.model.Booking;
import com.airline.pnr.services.BookingAggregatorQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


/*
Vert.x Future<Booking>
        ↓  (Vert.x adapter)
CompletionStage<Booking>
        ↓  (Reactor adapter)
Mono<Booking>

 */
@Service
public class BookingQueryAdapter {
    
    private static final Logger log = LoggerFactory.getLogger(BookingQueryAdapter.class);
    
    private final BookingAggregatorQueryService service;
    
    public BookingQueryAdapter(BookingAggregatorQueryService service) {
        this.service = service;
    }
    
    public Mono<Booking> execute(String pnr) {

        return Mono.fromCompletionStage( // from CompletionStage to Mono
                // Future<Booking>.toCompletionStage
                service.execute(pnr).toCompletionStage() // from Future of vert.x to toCompletionStage
        );
    }
}