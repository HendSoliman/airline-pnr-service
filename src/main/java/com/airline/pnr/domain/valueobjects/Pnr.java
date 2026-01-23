package com.airline.pnr.domain.valueobjects;

import java.util.Objects;
import java.util.regex.Pattern;

public record Pnr(String value) {
    
    private static final Pattern PNR_PATTERN = Pattern.compile("^[A-Z0-9]{6}$");
    
    public Pnr {
        Objects.requireNonNull(value, "PNR cannot be null");
        if (!PNR_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid PNR format: " + value);
        }
    }
    
}