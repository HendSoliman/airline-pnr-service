package com.airline.pnr.infrastructure;


import com.airline.pnr.application.contract.BookingDomainRepo;
import com.airline.pnr.config.ThreadLog;
import com.airline.pnr.model.Booking;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class BookingRepositoryImpl implements BookingDomainRepo {
    
    private static final Logger log = LoggerFactory.getLogger(BookingRepositoryImpl.class);
    
    private final MongoClient mongoClient;
    
    public BookingRepositoryImpl(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }
    
    
    @Override
    public Future<Booking> findByPnr(String pnr) {
        
        log.debug("Repo ENTER pnr={} | {}", pnr, ThreadLog.current());
//        log.debug("Repository accessing MongoDB for Booking...");
        
        
        JsonObject query = new JsonObject().put("bookingReference", pnr);
        
        return mongoClient.findOne("bookings", query, null)
                          .onSuccess(json ->
                                  log.debug("Mongo result received | {}", ThreadLog.current())
                          )
                          .onFailure(err ->
                                  log.error("Mongo failure | {}", ThreadLog.current(), err)
                          )
                          .map(json -> json.mapTo(Booking.class));
    }

}
