package uz.agrobank.authservice.resources;

import uz.agrobank.authservice.domain.User;
import uz.agrobank.authservice.dto.LoginForm;
import uz.agrobank.authservice.dto.ResponseMessage;
import uz.agrobank.authservice.security.AuthTokenProvider;
import uz.agrobank.authservice.security.ZuulTokenValidator;
import uz.agrobank.authservice.security.Device;
import uz.agrobank.authservice.services.UserService;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/users")
@Slf4j
public class LoginController {

    @Autowired
    private AuthTokenProvider tokenProvider;
    
    @Autowired
    private ZuulTokenValidator zuulTokenValidator;

    @Autowired
    private UserService userService;

    @PostMapping(path = "/login", produces = "application/json;charset=UTF-8")
    @CrossOrigin
    public ResponseEntity<?> login(@RequestBody LoginForm login, @RequestHeader(value = "zuul-token", required = true) String zuulToken) {

        Boolean validateToken = zuulTokenValidator.validateToken(zuulToken);
        if (!validateToken) {
            return ResponseEntity.badRequest().body(new ResponseMessage("Zuul Token is not valid"));
        }

        Optional<User> loggedUser = userService.login(login);

        if (loggedUser.isPresent()) {
            HttpHeaders headers = new HttpHeaders();
            final Device device = new Device(true, false, false);
            headers.add("AUTH-TOKEN", tokenProvider.generateToken(loggedUser.get().getPhone(), device));
            headers.add("Authorization", tokenProvider.generateToken(loggedUser.get().getPhone(), device));
            return ResponseEntity.ok().headers(headers).body(loggedUser.get());
        }
        if (!loggedUser.isPresent()) {
            return ResponseEntity.badRequest().body(new ResponseMessage("User does not exist"));
        }
        return ResponseEntity.notFound().build();

    }

}
