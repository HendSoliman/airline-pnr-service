package com.airline.pnr.infrastructure;


import com.airline.pnr.services.contract.TicketDomainRepo;
import io.vertx.core.Future;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class TicketRepositoryImpl implements TicketDomainRepo {
    
    @Override
    public Future<Map<Integer, String>> findTicketUrls(List<Integer> passengerIds, String pnr) {
        return null;
    }
}
