package com.airline.pnr.config;


import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.jackson.DatabindCodec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VertxEngineConfig {

//    @Value("${spring.data.mongodb.uri}")
//    private String mongoUri;
    
    @Bean
    public Vertx vertx() {
        
        VertxOptions options = new VertxOptions()
                .setBlockedThreadCheckInterval(1000)  // check every 1s
                .setWarningExceptionTime(1000);       // warn if blocked > 1s
        
        return Vertx.vertx(options);
        
    }
    static {
        DatabindCodec.mapper().registerModule(new JavaTimeModule())
                     .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }
//    @Bean
//    public MongoClient vertxMongoClient(Vertx vertx) {
//        JsonObject config = new JsonObject().put("connection_string", mongoUri) // Use the injected URI
//                                            .put("db_name", "pnrdb");
//        return MongoClient.createShared(vertx, config);
//    }
}