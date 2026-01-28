package com.airline.pnr.services.contract;

import io.vertx.core.Future;

import java.util.List;
import java.util.Map;

public interface TicketDomainRepo {
    //  a Map of PassengerId -> TicketUrl
    Future<Map<Integer, String>> findTicketUrls(List<Integer> passengerIds, String pnr);
    
}