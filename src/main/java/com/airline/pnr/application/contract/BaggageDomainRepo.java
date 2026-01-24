package com.airline.pnr.application.contract;

import com.airline.pnr.model.BaggageAllowance;

import java.util.List;

public interface BaggageDomainRepo {
    
    List<BaggageAllowance> findBaggagesOfPassengers(List<Integer> passengerIds, String pnr);
}
