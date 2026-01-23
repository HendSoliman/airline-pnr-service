package com.airline.pnr.domain.exception;

public class BookingNotFoundException extends PnrDomainException {
    
    public BookingNotFoundException(String pnr) {
        super("Booking Not Found",
                "No booking exists for the provided reference: " + pnr,
                404);
    }
}