package com.airline.pnr.controller;

import com.airline.pnr.application.BookingAggregatorQueryService;
import com.airline.pnr.domain.ThreadLog;
import com.airline.pnr.model.Booking;
import com.airline.pnr.openapi.api.BookingApi;
import com.airline.pnr.openapi.model.BookingResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PnrQueryController implements BookingApi {
    private static final Logger log = LoggerFactory.getLogger(PnrQueryController.class);
    
    private final BookingAggregatorQueryService bookingInfoQueryService;
    private final PnrResponseMapper mapper;
    
    public PnrQueryController(BookingAggregatorQueryService bookingInfoQueryService,
                              PnrResponseMapper mapper) {
        this.bookingInfoQueryService = bookingInfoQueryService;
        this.mapper = mapper;
    }
    
    @Override
    public ResponseEntity<BookingResponse> getBookingByPnr(String pnr) {
   
        ThreadLog.log(log,"Controller ENTER pnr=%s", pnr);
        
        Booking bookingInfoResult = bookingInfoQueryService.execute(pnr);
        BookingResponse response = mapper.toResponse(bookingInfoResult);
        
        ThreadLog.log(log, "Returning response for pnr=%s", pnr);
        return ResponseEntity.ok(response);
    }
}