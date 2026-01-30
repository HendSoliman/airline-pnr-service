package com.airline.pnr.services.contract;

import com.airline.pnr.domain.events.BookingEvent;

public interface EventPublisherPort {
    void publish(BookingEvent event);
}