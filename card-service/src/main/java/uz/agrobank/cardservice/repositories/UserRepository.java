package uz.agrobank.cardservice.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import uz.agrobank.cardservice.domain.User;

import java.util.Optional;

@Repository
@Component
public interface UserRepository
        extends MongoRepository<User, String> {

    Optional<User> findByPhoneAndPassword(String phone, String password);

    Optional<User> findByPhone(String phone);

}
