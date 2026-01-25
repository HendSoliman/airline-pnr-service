package com.airline.pnr.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.time.Instant;

public class MongoInstantDeserializer extends JsonDeserializer<Instant> {
    @Override
    public Instant deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        
        // If it's the Vert.x/Mongo object: {"$date": "..."}
        if (node.isObject() && node.has("$date")) {
            return Instant.parse(node.get("$date").asText());
        }
        
        // If it's already a plain string: "2025-11-11T..."
        return Instant.parse(node.asText());
    }
}