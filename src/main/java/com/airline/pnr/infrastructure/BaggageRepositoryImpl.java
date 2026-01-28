package com.airline.pnr.infrastructure;

import com.airline.pnr.infrastructure.access.ReactiveBaggageRepository;
import com.airline.pnr.infrastructure.entities.BaggageAllowanceEntity;
import com.airline.pnr.model.BaggageAllowance;
import com.airline.pnr.services.contract.BaggageDomainRepo;
import io.vertx.core.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BaggageRepositoryImpl implements BaggageDomainRepo {
    private static final Logger log = LoggerFactory.getLogger(BaggageRepositoryImpl.class);
    
    private final ReactiveBaggageRepository repo;
    
    public BaggageRepositoryImpl(ReactiveBaggageRepository repo) {
        this.repo = repo;
    }
    
    @Override
    public Future<List<BaggageAllowance>> findBagsOfPassengers(List<Integer> passengerIds, String pnr) {
        log.debug("Repository accessing MongoDB for baggage...");
        
        
        return Future.fromCompletionStage(
                repo.findByBookingReferenceAndPassengerNumberIn(pnr, passengerIds) // Returns Flux<BaggageAllowanceEntity>
                    .map(this::mapToDomain )
                    .collectList()
                    .toFuture()
        );
        
    }
    /**
     * Maps the MongoDB Entity record to the Model BaggageAllowance record
     */
    private BaggageAllowance mapToDomain(BaggageAllowanceEntity entity) {
        return new BaggageAllowance(
                entity.allowanceUnit(),
                entity.checkedAllowanceValue(),
                entity.carryOnAllowanceValue()
        );
    }
    
}