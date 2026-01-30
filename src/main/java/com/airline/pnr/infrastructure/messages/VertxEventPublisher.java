package com.airline.pnr.infrastructure.messages;

import com.airline.pnr.domain.events.BookingEvent;
import com.airline.pnr.domain.events.PnrFetchedEvent;
import com.airline.pnr.services.BookingAggregatorQueryService;
import com.airline.pnr.services.contract.EventPublisherPort;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class VertxEventPublisher implements EventPublisherPort {
    private static final Logger log = LoggerFactory.getLogger(VertxEventPublisher.class);
    
    final Vertx vertx;
    
    public VertxEventPublisher(Vertx vertx) {
        this.vertx = vertx;
    }
    
    @Override
    public void publish(BookingEvent event) {
        log.info("Publishing booking event.... {}", event);
        
        JsonObject message = new JsonObject().put("pnr", event.pnr());
        vertx.eventBus().publish(getEventTopic(event), message);
        log.info("Publishing booking event Done at topic: {}", getEventTopic(event));
    }
    
    private String getEventTopic(BookingEvent event) {
        return switch (event) {
            case PnrFetchedEvent ev -> "pnr.fetched";
            
        };
    }
    
    
}
