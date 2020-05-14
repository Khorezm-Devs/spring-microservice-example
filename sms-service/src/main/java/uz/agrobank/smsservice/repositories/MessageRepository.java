package uz.agrobank.smsservice.repositories;

import uz.agrobank.smsservice.domain.Message;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
@Component
public interface MessageRepository
        extends MongoRepository<Message, String> {

    Optional<List<Message>> findByPhone(String phone);

    Optional<Message> findByPhoneAndCode(String phone, String code);

    Optional<Message> findTopByPhoneOrderByCreatedAtDesc(String phone);

}
