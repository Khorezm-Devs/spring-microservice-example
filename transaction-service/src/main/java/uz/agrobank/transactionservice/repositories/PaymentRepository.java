package uz.agrobank.transactionservice.repositories;

import uz.agrobank.transactionservice.domain.Payment;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

/**
 *
 * @author armena
 */
@Repository
@Component
public interface PaymentRepository
        extends MongoRepository<Payment, String> {

    Optional<List<Payment>> findAllByUserId(String userId);
    
    Optional<Payment> findAllByCardId(String id);

    Optional<Payment> findById(String id);
}
