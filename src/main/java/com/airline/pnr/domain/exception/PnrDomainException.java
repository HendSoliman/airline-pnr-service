package com.airline.pnr.domain.exception;

public class PnrDomainException extends RuntimeException {
    private final String title;
    private final int status;
    
    protected PnrDomainException(String title, String message, int status) {
        super(message);
        this.title = title;
        this.status = status;
    }
    
    public String getTitle() { return title; }
    public int getStatus() { return status; }
}
