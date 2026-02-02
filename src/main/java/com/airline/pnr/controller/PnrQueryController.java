package com.airline.pnr.controller;

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
    
    private final BookingQueryAdapter bookingQueryAdapter;
    private final BookingResponseMapper bookingResponseMapper;
    
    public PnrQueryController(BookingQueryAdapter bookingQueryAdapter, BookingResponseMapper bookingResponseMapper) {
        this.bookingQueryAdapter = bookingQueryAdapter;
        this.bookingResponseMapper = bookingResponseMapper;
    }
    
    @Override
    public Mono<ResponseEntity<BookingResponse>> getBookingByPnr(
            String pnr,
            ServerWebExchange exchange
    ) {
        log.debug("starting http call of pnr query for pnr {}", pnr);
        
        return bookingQueryAdapter.execute(pnr)
                                  .map(bookingResponseMapper::toResponse)
                                  .map(ResponseEntity::ok);
    }
}

