package com.airline.pnr.domain.valueobjects;

import com.fasterxml.jackson.annotation.JsonValue;

public record CustomerId(@JsonValue String value) {
    
    public static CustomerId ofNullable(String value) {
        return value == null || value.isBlank() ? null : new CustomerId(value);
    }
    
}