package com.airline.pnr.domain.events;


public record PnrFetchedEvent(String pnr) implements BookingEvent {
}