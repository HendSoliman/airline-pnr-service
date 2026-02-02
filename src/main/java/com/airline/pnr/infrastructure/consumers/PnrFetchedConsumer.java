package com.airline.pnr.infrastructure.consumers;


import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PnrFetchedConsumer {
    private static final Logger log = LoggerFactory.getLogger(PnrFetchedConsumer.class);
    
    PnrFetchedConsumer(Vertx vertx) {
        // log
        log.debug("Initializing PnrFetchedConsumer...");
//        then
        vertx.eventBus().consumer("pnr.fetched", message -> {
            // received message
            JsonObject body = (JsonObject) message.body();
            log.info("Received fetched message from {}", "pnr.fetched");
            String pnr = body.getString("pnr");
            log.info("PNR fetched event received: {}", pnr);
        });
        
    }
}

