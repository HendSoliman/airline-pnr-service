package com.airline.pnr.domain.model;

public record BaggageAllowance(
        String allowanceUnit,
        int checkedAllowanceValue,
        int carryOnAllowanceValue
)
{
}
