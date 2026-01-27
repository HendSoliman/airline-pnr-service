package com.airline.pnr.infrastructure.message;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.springframework.stereotype.Component;

@Component
public class VertxEventPublisher {
    
    private final Vertx vertx;
    
    public VertxEventPublisher(Vertx vertx) {
        this.vertx = vertx;
    }
    
    /**
     * Publish an event when a PNR is fetched.
     */
    public void publishPnrFetched(String pnr) {
        JsonObject message = new JsonObject().put("pnr", pnr);
        vertx.eventBus().publish("pnr.fetched", message);
    }
    
    
}
