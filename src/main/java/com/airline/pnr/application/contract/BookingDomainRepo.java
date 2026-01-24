package com.airline.pnr.application.contract;

import com.airline.pnr.model.Booking;

public interface BookingDomainRepo {
    Booking findByPnr(String pnr);
}
