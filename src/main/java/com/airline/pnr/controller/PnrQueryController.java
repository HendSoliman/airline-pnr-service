package com.airline.pnr.controller;

import com.airline.pnr.application.BookingAggregatorQueryService;
import com.airline.pnr.model.Booking;
import com.airline.pnr.openapi.api.BookingApi;
import com.airline.pnr.openapi.model.BookingResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PnrQueryController implements BookingApi {
    
    private final BookingAggregatorQueryService bookingInfoQueryService;
    private final PnrResponseMapper mapper;
    
    public PnrQueryController(BookingAggregatorQueryService bookingInfoQueryService, PnrResponseMapper mapper) {
        this.bookingInfoQueryService = bookingInfoQueryService;
        this.mapper = mapper;
    }
    
    @Override
    public ResponseEntity<BookingResponse> getBookingByPnr(String pnr) {
        Booking bookingInfoResult = bookingInfoQueryService.execute(pnr);
        var bookingInfoResultOpt = mapper.toResponse(bookingInfoResult);
        
        return ResponseEntity.ok(bookingInfoResultOpt);
    }
    
}
