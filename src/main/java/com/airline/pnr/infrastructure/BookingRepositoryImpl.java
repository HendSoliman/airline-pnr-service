package com.airline.pnr.infrastructure;


import com.airline.pnr.application.contract.BookingDomainRepo;
import com.airline.pnr.domain.exception.BookingNotFoundException;
import com.airline.pnr.infrastructure.db.BookingMongoRepository;
import com.airline.pnr.infrastructure.mapper.PnrDbMapper;
import com.airline.pnr.model.Booking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class BookingRepositoryImpl implements BookingDomainRepo {
    
    private static final Logger log = LoggerFactory.getLogger(BookingRepositoryImpl.class);
    
    private final BookingMongoRepository mongoRepo;
    
    private final PnrDbMapper mapper;
    
    public BookingRepositoryImpl(BookingMongoRepository mongoRepo, PnrDbMapper mapper) {
        this.mongoRepo = mongoRepo;
        this.mapper = mapper;
    }
    
    @Override
    public Booking findByPnr(String pnr) {
        log.debug("Repository accessing MongoDB for Booking...");
  
        return  mongoRepo.findByBookingReference(pnr)
                         .map(mapper::toReadModel)
                         .orElseThrow(() -> new BookingNotFoundException(pnr));
        
    }
}