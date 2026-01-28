package com.airline.pnr.model;

import com.airline.pnr.domain.valueobjects.CustomerId;

public record Passenger(
        int passengerNumber,
        String firstName,
        String middleName,
        String lastName,
        CustomerId customerId,
        String seat,
        String ticketUrl,
        BaggageAllowance baggage
)
{
    
    
    public Passenger withDetails(BaggageAllowance bag, String url) {
        return new Passenger(
                this.passengerNumber,
                this.firstName,
                this.middleName,
                this.lastName,
                this.customerId,
                this.seat,
                url,
                bag
        );
    }
}