package com.airline.pnr.domain.valueobjects;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Objects;
import java.util.regex.Pattern;

public record Pnr(@JsonValue String value) {
    
    private static final Pattern PNR_PATTERN = Pattern.compile("^[A-Z0-9]{6}$");
    
    public Pnr {
        Objects.requireNonNull(value, "PNR cannot be null");
        if (!PNR_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid PNR format: " + value);
        }
    }
    
}