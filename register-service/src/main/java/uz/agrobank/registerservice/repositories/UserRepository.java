package uz.agrobank.registerservice.repositories;

import uz.agrobank.registerservice.domain.User;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
@Component
public interface UserRepository
        extends MongoRepository<User, String> {

    Optional<User> findByPhoneAndPassword(String phone, String password);
    
    Optional<User> findByPhone(String phone);

}
