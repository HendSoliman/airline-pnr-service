package com.airline.pnr.infrastructure;


import com.airline.pnr.application.contract.TicketDomainRepo;
import com.airline.pnr.infrastructure.db.TicketMongoRepository;
import com.airline.pnr.infrastructure.entities.TicketEntity;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class TicketRepositoryImpl implements TicketDomainRepo {
    
    private static final Logger log = LoggerFactory.getLogger(TicketRepositoryImpl.class);
    public static final String TICKETS_DB = "tickets";
    
    private final MongoClient mongoClient;
    
    public TicketRepositoryImpl(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }
    
    
    @Override
    public Future<Map<Integer, String>> findTicketUrls(List<Integer> ids, String pnr) {
        
        log.debug("Repository accessing MongoDB for Ticket...");
        
        // 1. Build the async query
        JsonObject query = new JsonObject()
                .put("passengerNumber", new JsonObject().put("$in", new JsonArray(ids)))
                .put("bookingReference", pnr);
        
        // 2. Execute and transform the result stream
        return mongoClient.find(TICKETS_DB, query)
                          .map(list -> list.stream()
                                           // Map JsonObject to your Ticket Record/Entity
                                           .map(json -> json.mapTo(TicketEntity.class))
                                           // Filter and Collect exactly like your snippet
                                           .filter(ticket -> ticket.ticketUrl().isPresent())
                                           .collect(Collectors.toMap(
                                                   TicketEntity::passengerNumber,
                                                   ticket -> ticket.ticketUrl().get()
                                           ))
                          );
    }
    
    
}