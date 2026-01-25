package com.airline.pnr.application.contract;

import com.airline.pnr.model.BaggageAllowance;
import io.vertx.core.Future;

import java.util.List;

public interface BaggageDomainRepo {
    
    Future<List<BaggageAllowance>> findBaggagesOfPassengers(List<Integer> passengerIds, String pnr);
}
