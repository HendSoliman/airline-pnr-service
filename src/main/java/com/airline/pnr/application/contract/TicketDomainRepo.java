package com.airline.pnr.application.contract;

import java.util.List;
import java.util.Map;

public interface TicketDomainRepo {
    //  a Map of PassengerId -> TicketUrl
    Map<Integer, String> findTicketUrls(List<Integer> passengerIds, String pnr);
    
}
