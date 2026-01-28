package com.airline.pnr.domain.valueobjects;

public record CustomerId(String value) {
    
    public static CustomerId ofNullable(String value) {
        return value == null || value.isBlank() ? null : new CustomerId(value);
    }
    
}