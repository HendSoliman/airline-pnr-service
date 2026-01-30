package com.airline.pnr.domain.events;

public sealed interface BookingEvent permits PnrFetchedEvent {
    String pnr();
}

