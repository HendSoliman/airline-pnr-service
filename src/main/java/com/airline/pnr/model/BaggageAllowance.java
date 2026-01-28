package com.airline.pnr.model;

public record BaggageAllowance(
        String allowanceUnit,
        int checkedAllowanceValue,
        int carryOnAllowanceValue
)
{
}
