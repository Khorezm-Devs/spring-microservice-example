package uz.agrobank.transactionservice.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import uz.agrobank.transactionservice.domain.Card;

import java.util.List;
import java.util.Optional;

@Repository
@Component
public interface CardRepository
        extends MongoRepository<Card, String> {

    Optional<List<Card>> findAllByUserId(String userId);

    Optional<Card> findByPan(String cardNumber);
}
