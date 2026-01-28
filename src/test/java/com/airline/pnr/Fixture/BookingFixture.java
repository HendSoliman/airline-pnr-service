package com.airline.pnr.Fixture;

import com.airline.pnr.domain.valueobjects.CustomerId;
import com.airline.pnr.domain.valueobjects.Pnr;
import com.airline.pnr.model.BaggageAllowance;
import com.airline.pnr.model.Booking;
import com.airline.pnr.model.Flight;
import com.airline.pnr.model.Passenger;

import java.time.Instant;
import java.util.List;

public class BookingFixture {
        public static Booking ofPnr(String pnr) {
            
            var flight = new Flight(
                    "EK231", "DXB", Instant.parse("2025-11-11T02:25:00Z"),
                    "IAD", Instant.parse("2025-11-11T08:25:00Z")
            );
            
            var james = new Passenger(
                    1, "James", null, "McGill", new CustomerId(null),
                    "32D", null, new BaggageAllowance(1,"kg", 30, 7)
            );
            
            var charles = new Passenger(
                    2, "Charles", null, "McGill", new CustomerId("1216"),
                    "31D", "emirates.com?ticket=someTicketRef", null
            );
            
            return new Booking(new Pnr("GHTW42"), "ECONOMY", List.of(james, charles), List.of(flight), Instant.now());
        }
    }