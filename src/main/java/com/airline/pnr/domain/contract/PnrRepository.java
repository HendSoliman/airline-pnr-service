package com.airline.pnr.domain.contract;

import com.airline.pnr.domain.model.BookingInformation;

import java.util.concurrent.CompletableFuture;

public interface PnrRepository {
    CompletableFuture<BookingInformation> findBookingInfoByPnr(String pnr);
}
