package com.airline.pnr.domain.valueobjects;
import java.util.Optional;

public record CustomerId(String value) {
    
    public static CustomerId ofNullable(String value) {
        return value == null || value.isBlank() ? null : new CustomerId(value);
    }
    
    public Optional<String> asOptional() {
        return Optional.ofNullable(value);
    }
}