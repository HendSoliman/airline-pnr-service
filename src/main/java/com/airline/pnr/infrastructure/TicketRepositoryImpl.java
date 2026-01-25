package com.airline.pnr.infrastructure;


import com.airline.pnr.application.contract.TicketDomainRepo;
import com.airline.pnr.infrastructure.db.ReactiveBookingRepository;
import com.airline.pnr.infrastructure.db.ReactiveTicketRepository;
import com.airline.pnr.infrastructure.entities.TicketEntity;
import com.mongodb.reactivestreams.client.MongoClient;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
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
    
    private final ReactiveTicketRepository repo;
    
    public TicketRepositoryImpl(ReactiveTicketRepository repo) {
        this.repo = repo;
    }
    
    
    @Override
    public Future<Map<Integer, String>> findTicketUrls(List<Integer> ids, String pnr) {
        
        log.debug("Repository accessing MongoDB for Ticket...");
        
        return Future.fromCompletionStage(
                repo.findByBookingReferenceAndPassengerNumberIn(pnr, ids)
                          // 1. Filter tickets that actually have a URL
                          .filter(ticket -> ticket.ticketUrl().isPresent())
                          // 2. Collect into a Map<Integer, String>
                          .collectMap(
                                  TicketEntity::passengerNumber,
                                  ticket -> ticket.ticketUrl().get()
                          )
                          // 3. Convert Mono<Map> to Java Future
                          .toFuture()
        );
    
    
}}