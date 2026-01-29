package com.airline.pnr.controller;

import com.airline.pnr.openapi.api.BookingApi;
import com.airline.pnr.openapi.model.BookingResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
public class PnrQueryController implements BookingApi {
    
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
        return bookingQueryAdapter.execute(pnr)
                                  .map(bookingResponseMapper::toResponse)
                                  .map(ResponseEntity::ok);
    }
}

