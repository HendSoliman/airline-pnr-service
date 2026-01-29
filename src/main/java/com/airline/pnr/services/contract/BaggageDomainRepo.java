package com.airline.pnr.services.contract;

import com.airline.pnr.model.BaggageAllowance;
import io.vertx.core.Future;

import java.util.List;

public interface BaggageDomainRepo {
    
    Future<List<BaggageAllowance>> findBagsOfPassengers(List<Integer> passengerIds, String pnr);
}
