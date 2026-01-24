package com.airline.pnr.infrastructure;


import com.airline.pnr.application.contract.BaggageDomainRepo;
import com.airline.pnr.infrastructure.db.BaggageMongoRepository;
import com.airline.pnr.infrastructure.entities.BaggageAllowanceEntity;
import com.airline.pnr.model.BaggageAllowance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BaggageRepositoryImpl implements BaggageDomainRepo {
    
    private static final Logger log = LoggerFactory.getLogger(BaggageRepositoryImpl.class);
    
    private final BaggageMongoRepository mongoRepo;
    
    public BaggageRepositoryImpl(BaggageMongoRepository mongoRepo) {
        this.mongoRepo = mongoRepo;
    }
    
    
    @Override
    public List<BaggageAllowance> findBaggagesOfPassengers(List<Integer> ids, String pnr) {
        log.debug("Repository accessing MongoDB for baggage...");
        var baggages = mongoRepo.findAllByPassengerIdsAndBookingReference(ids, pnr);
        return baggages.stream().map(this::toBaggageDomain).toList();
    }
    
    
    BaggageAllowance toBaggageDomain(BaggageAllowanceEntity BaggageAllowanceEntity) {
        return new BaggageAllowance(BaggageAllowanceEntity.passengerNumber(), BaggageAllowanceEntity.allowanceUnit(), BaggageAllowanceEntity.checkedAllowanceValue(), BaggageAllowanceEntity.carryOnAllowanceValue());
    }
}
