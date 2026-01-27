package com.airline.pnr.controller;

import com.airline.pnr.application.BookingAggregatorQueryService;
import com.airline.pnr.openapi.api.BookingApi;
import com.airline.pnr.openapi.model.BookingResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
public class PnrQueryController implements BookingApi {
    private static final Logger log = LoggerFactory.getLogger(PnrQueryController.class);
    
    private final BookingAggregatorQueryService bookingInfoQueryService;
    private final BookingResponseMapper mapper;
    
    public PnrQueryController(BookingAggregatorQueryService bookingInfoQueryService, BookingResponseMapper mapper) {
        this.bookingInfoQueryService = bookingInfoQueryService;
        this.mapper = mapper;
    }
    
    @Override
    public Mono<ResponseEntity<BookingResponse>> getBookingByPnr(String pnr, ServerWebExchange exchange) {
        log.info("Fetching  pnr={} ", pnr);
        return Mono.fromCompletionStage(
                           bookingInfoQueryService.execute(pnr)
                                                  // CRITICAL: Map to response BEFORE leaving Vert.x context
                                                  .map(mapper::toResponse)
                                                  .toCompletionStage()
                   )
                   
                   .map(ResponseEntity::ok)
                   .doOnNext(res -> log.info("[Controller] READY pnr={} | Thread={}", pnr, Thread.currentThread().getName()))
                   .doFinally(sig -> log.info("[Controller] DONE pnr={} | Signal={} | Thread={}", pnr, sig, Thread.currentThread().getName()));
    }
}
