package com.airline.pnr.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BaggageAllowance(
        int passengerNumber,
        String allowanceUnit,
        int checkedAllowanceValue,
        int carryOnAllowanceValue
)
{
}
