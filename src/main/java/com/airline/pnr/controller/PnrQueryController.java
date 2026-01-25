package com.airline.pnr.controller;

import com.airline.pnr.application.BookingAggregatorQueryService;
import com.airline.pnr.model.Booking;
import com.airline.pnr.openapi.api.BookingApi;
import com.airline.pnr.openapi.model.BookingResponse;
import io.vertx.core.Future;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
public class PnrQueryController implements BookingApi {
    
    private final BookingAggregatorQueryService bookingInfoQueryService;
    private final PnrResponseMapper mapper;

    public PnrQueryController(BookingAggregatorQueryService bookingInfoQueryService, PnrResponseMapper mapper) {
        this.bookingInfoQueryService = bookingInfoQueryService;
        this.mapper = mapper;
    }

    @Override
    public Mono<ResponseEntity<BookingResponse>> getBookingByPnr(String pnr, ServerWebExchange exchange) {
        Future<Booking> bookingInfoResult =bookingInfoQueryService.execute(pnr);
        // 2. Convert to Mono and map to the response
        return Mono.fromCompletionStage(bookingInfoResult.toCompletionStage())
                   .map(booking -> ResponseEntity.ok(mapper.toResponse(booking)))
                   .defaultIfEmpty(ResponseEntity.notFound().build());
        
        
        
    }
    
}
