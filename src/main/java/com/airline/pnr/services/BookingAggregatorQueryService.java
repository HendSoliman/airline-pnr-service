package com.airline.pnr.services;

import com.airline.pnr.model.Booking;
import io.vertx.core.Future;
import org.springframework.stereotype.Service;

@Service
public class BookingAggregatorQueryService {
    public Future<Booking> execute(String pnr) {
  
        return Future.succeededFuture();
    }
}
