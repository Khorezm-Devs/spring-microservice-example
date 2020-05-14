package uz.agrobank.registerservice.services;

import uz.agrobank.registerservice.domain.User;
import uz.agrobank.registerservice.repositories.UserRepository;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Optional<User> registerNewUser(User user) {
        if (this.getUser(user.getPhone()).isPresent()) {
            return Optional.empty();
        }
        user.setId(null);
        User registeredUser = userRepository.save(user);
        return Optional.ofNullable(registeredUser);
    }

    public Optional<User> getUser(String phone) {
        return userRepository.findByPhone(phone);
    }
}
