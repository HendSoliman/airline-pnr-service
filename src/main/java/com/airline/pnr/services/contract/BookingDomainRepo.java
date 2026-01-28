package com.airline.pnr.services.contract;

import com.airline.pnr.model.Booking;
import io.vertx.core.Future;

public interface BookingDomainRepo {
    Future<Booking> findByPnr(String pnr);
}