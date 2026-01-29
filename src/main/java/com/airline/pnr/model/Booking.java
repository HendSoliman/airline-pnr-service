package com.airline.pnr.model;

import com.airline.pnr.domain.valueobjects.Pnr;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record Booking(
        Pnr bookingReference,
        String cabinClass,
        List<Passenger> passengers,
        List<Flight> flights,
        Instant updatedAt
)
{
    public Booking withDetails(List<BaggageAllowance> bags, Map<Integer, String> tickets) {
        List<Passenger> enrichedPassengers = this.passengers.stream()
                                                            .map(passenger -> {
                                                                // Find baggage for this specific passenger ID
                                                                BaggageAllowance matchingBag = bags.stream()
                                                                                                   .filter(bag -> bag.passengerNumber() == passenger.passengerNumber())
                                                                                                   .findFirst()
                                                                                                   .orElse(null);
                                                                
                                                                // Find ticket URL for this specific passenger ID
                                                                String ticketUrl = tickets.get(passenger.passengerNumber());
                                                                
                                                                return passenger.withDetails(matchingBag, ticketUrl);
                                                            })
                                                            .toList();
        
        // Return a new Booking record with all related bags and tickets populated
        return new Booking(
                this.bookingReference,
                this.cabinClass,
                enrichedPassengers,
                this.flights,
                this.updatedAt
        );
    }
}
