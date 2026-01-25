package com.airline.pnr.infrastructure;


import com.airline.pnr.application.contract.BaggageDomainRepo;
import com.airline.pnr.infrastructure.db.ReactiveBaggageRepository;
import com.airline.pnr.model.BaggageAllowance;
import io.vertx.core.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
public class BaggageRepositoryImpl implements BaggageDomainRepo {
    
    private static final Logger log = LoggerFactory.getLogger(BaggageRepositoryImpl.class);
    public static final String BAGGAGE_ALLOWANCES_DB = "baggage_allowances";
    
    private final ReactiveBaggageRepository repo;
    
    public BaggageRepositoryImpl(ReactiveBaggageRepository repo) {
        this.repo = repo;
    }
    
    
    @Override
    public Future<List<BaggageAllowance>> findBaggagesOfPassengers(List<Integer> ids, String pnr) {
        log.debug("Repository accessing MongoDB for baggage...");
        
        
        return Future.fromCompletionStage(
                repo.findByBookingReferenceAndPassengerNumberIn(pnr,ids) // 1. Returns Flux<BaggageAllowanceEntity>
                    
                    
                    // Inline mapping: Entity -> Domain Record
                    .map(entity -> new BaggageAllowance(
                            entity.passengerNumber(),
                            entity.allowanceUnit(),
                            entity.checkedAllowanceValue(),
                            entity.carryOnAllowanceValue()
                    ))
                    .collectList()
                    .toFuture()
        );
        

    
    }
    
    
}
