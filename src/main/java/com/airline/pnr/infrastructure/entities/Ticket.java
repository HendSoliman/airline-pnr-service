package com.airline.pnr.infrastructure.entities;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Optional;

@Document("tickets")
@CompoundIndex(name = "pnr_passenger_idx", def = "{'bookingReference': 1, 'passengerNumber': 1}")
public record Ticket(
        @Id
        String id,
        String bookingReference,
        int passengerNumber,
        Optional<String> ticketUrl
) {}