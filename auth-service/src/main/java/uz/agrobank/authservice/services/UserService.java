package uz.agrobank.authservice.services;

import uz.agrobank.authservice.domain.User;
import uz.agrobank.authservice.dto.LoginForm;
import uz.agrobank.authservice.repositories.UserRepository;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class UserService {
    
     @Autowired
     private UserRepository userRepository;
     
     public Optional<User> login(LoginForm login){
         return userRepository.findByPhoneAndPassword(login.getPhone(), login.getPassword());
     }
     
     public Optional<User> findByPhone(String phone){
         return userRepository.findByPhone(phone);
     }
     
     
     
}
