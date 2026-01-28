package com.airline.pnr.infrastructure;

import com.airline.pnr.infrastructure.access.ReactiveBaggageRepository;
import com.airline.pnr.infrastructure.entities.BaggageAllowanceEntity;
import com.airline.pnr.model.BaggageAllowance;
import io.vertx.core.Future;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;


@ExtendWith({MockitoExtension.class, VertxExtension.class})
class BaggageRepositoryImplTest {
    
    @Mock
    private ReactiveBaggageRepository repo;
    
    @InjectMocks
    private BaggageRepositoryImpl baggageRepositoryImp;
    
    @Test
    @DisplayName(" Should return Future of BaggageAllowances for given passenger IDs and PNR")
    void should_return_future_of_bags(VertxTestContext testContext) {
        // Arrange
        String pnr = "GHTW42";
        List<Integer> passengerIds = List.of(1, 2);
        
        BaggageAllowanceEntity entity1 =
                new BaggageAllowanceEntity("1", pnr, 1, "kg", 30, 7);
        
        BaggageAllowanceEntity entity2 =
                new BaggageAllowanceEntity("2", pnr, 2, "kg", 20, 5);
        
        when(repo.findByBookingReferenceAndPassengerNumberIn(pnr, passengerIds))
                .thenReturn(Flux.just(entity1, entity2));
        
        // Act
        Future<List<BaggageAllowance>> result =
                baggageRepositoryImp.findBagsOfPassengers(passengerIds, pnr);
        
        
        // Assert 
        result.onComplete(testContext.succeeding(bags -> {
            testContext.verify(() -> {
                assertThat(bags).hasSize(2);
                // Verify mapping accuracy
                assertThat(bags.getFirst().allowanceUnit()).isEqualTo("kg");
                assertThat(bags.get(0).checkedAllowanceValue()).isEqualTo(30);
                assertThat(bags.get(0).carryOnAllowanceValue()).isEqualTo(7);
                assertThat(bags.get(1).allowanceUnit()).isEqualTo("kg");
                assertThat(bags.get(1).checkedAllowanceValue()).isEqualTo(20);
                assertThat(bags.get(1).carryOnAllowanceValue()).isEqualTo(5);
                
                testContext.completeNow();
            });
        }));
    }
}