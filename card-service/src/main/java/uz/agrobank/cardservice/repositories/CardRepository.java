package uz.agrobank.cardservice.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import uz.agrobank.cardservice.domain.Card;
import uz.agrobank.cardservice.domain.User;

import java.util.List;
import java.util.Optional;

@Repository
@Component
public interface CardRepository
        extends MongoRepository<Card, String> {

    Optional<List<Card>> findAllByUserId(String userId);

    Optional<Card> findByPan(String pan);
}
