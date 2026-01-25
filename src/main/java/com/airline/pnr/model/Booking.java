package com.airline.pnr.model;

import com.airline.pnr.domain.valueobjects.Pnr;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Booking(
        Pnr bookingReference,
        String cabinClass,
        List<Passenger> passengers,
        List<Flight> flights
)
{
    public Booking withDetails(List<BaggageAllowance> allBaggage, Map<Integer, String> allTickets) {
        List<Passenger> enrichedPassengers = this.passengers.stream().map(
                
                passenger -> {
            BaggageAllowance baggage = allBaggage.stream()
                                                 .filter(b -> b.passengerNumber() == passenger.passengerNumber())
                                                 .findFirst().orElse(null);
            
            String ticketUrl = allTickets.get(passenger.passengerNumber());
            
            return new Passenger(
                    passenger.passengerNumber(),
                    passenger.firstName(),
                    passenger.middleName(),
                    passenger.lastName(),
                    passenger.customerId(),
                    passenger.seat(),
                    ticketUrl,
                    baggage
            );
        }).toList();
        
        return new Booking(this.bookingReference, this.cabinClass, enrichedPassengers, this.flights);
    }
}