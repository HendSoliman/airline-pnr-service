package com.airline.pnr.model;

public record BaggageAllowance(
        int passengerNumber,
        String allowanceUnit,
        int checkedAllowanceValue,
        int carryOnAllowanceValue
)
{
}
