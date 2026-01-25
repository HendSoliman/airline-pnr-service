package com.airline.pnr.infrastructure;


import com.airline.pnr.application.contract.BaggageDomainRepo;
import com.airline.pnr.model.BaggageAllowance;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
public class BaggageRepositoryImpl implements BaggageDomainRepo {
    
    private static final Logger log = LoggerFactory.getLogger(BaggageRepositoryImpl.class);
    public static final String BAGGAGE_ALLOWANCES_DB = "baggage_allowances";
    
    private final MongoClient mongoClient;
    
    public BaggageRepositoryImpl(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }
    
    
    @Override
    public Future<List<BaggageAllowance>> findBaggagesOfPassengers(List<Integer> ids, String pnr) {
        log.debug("Repository accessing MongoDB for baggage...");
        
        JsonObject query = new JsonObject().put("passengerNumber", new JsonObject().put("$in", new JsonArray(ids))).put("bookingReference", pnr);
        
        
        return mongoClient.find(BAGGAGE_ALLOWANCES_DB, query).map(list -> {
            if (list == null || list.isEmpty()) {
                return Collections.emptyList();
            }
            
            return list.stream().map(json -> json.mapTo(BaggageAllowance.class)).toList();
        });
    }
    
    
}
