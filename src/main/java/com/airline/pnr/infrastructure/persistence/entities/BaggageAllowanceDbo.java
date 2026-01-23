package com.airline.pnr.infrastructure.persistence.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("baggage_allowances")
@CompoundIndex(name = "pnr_passenger_idx", def = "{'bookingReference': 1, 'passengerNumber': 1}")
public record BaggageAllowanceDbo(
        @Id
        String id,
        String bookingReference,
        int passengerNumber,
        String allowanceUnit,
        int checkedAllowanceValue,
        int carryOnAllowanceValue
) {}