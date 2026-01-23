package com.airline.pnr.controller;

import com.airline.pnr.domain.exception.BookingNotFoundException;
import com.airline.pnr.openapi.api.BookingApi;
import com.airline.pnr.openapi.model.BookingResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PnrController implements BookingApi {
    
    @Override
    public ResponseEntity<BookingResponse> getBookingByPnr(String pnr) {
        
        throw new BookingNotFoundException(pnr);
    }
  
}
