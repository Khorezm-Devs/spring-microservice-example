package uz.agrobank.registerservice.resources;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import uz.agrobank.registerservice.domain.User;
import uz.agrobank.registerservice.dto.RegisterForm;
import uz.agrobank.registerservice.dto.ResponseMessage;
import uz.agrobank.registerservice.security.AuthTokenProvider;
import uz.agrobank.registerservice.security.Device;
import uz.agrobank.registerservice.security.SmsValidator;
import uz.agrobank.registerservice.security.ZuulTokenValidator;
import uz.agrobank.registerservice.services.UserService;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/v2/users")
@Slf4j
public class RegisterController {

    @Autowired
    private ZuulTokenValidator zuulTokenValidator;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthTokenProvider tokenProvider;

    @Autowired
    private SmsValidator smsValidator;

    @PostMapping(path = "/register", produces = "application/json;charset=UTF-8")
    @CrossOrigin
    public ResponseEntity<?> register(@RequestBody RegisterForm form,
            @RequestHeader(value = "zuul-token", required = true) 
                    String zuulToken) {        
        
        Boolean validateToken = zuulTokenValidator.validateToken(zuulToken);

        if (!validateToken) {
            return ResponseEntity.badRequest().body(new ResponseMessage("Zuul Token is not valid"));
        }

        Boolean validateSms = smsValidator.validateCode(form.getPhone(), form.getVerification());
        if (!validateSms) {
            return ResponseEntity.badRequest().body(new ResponseMessage("Verification code is not valid"));
        }

        Optional<User> registeredUser = userService.registerNewUser(new User(form.getPhone(), form.getPassword()));

        if (registeredUser.isPresent()) {
            log.info("User is registered");
            HttpHeaders headers = new HttpHeaders();
            final Device device = new Device(true, false, false);
            headers.add("AUTH-TOKEN", tokenProvider.generateToken(registeredUser.get().getPhone(), device));
            headers.add("Authorization", tokenProvider.generateToken(registeredUser.get().getPhone(), device));
            return ResponseEntity.ok().headers(headers).body(registeredUser.get().getId());
        }

        if (!registeredUser.isPresent()) {
            return ResponseEntity.badRequest().body(new ResponseMessage("Could not registerservice user"));
        }

        return ResponseEntity.badRequest().build();

    }

    @GetMapping(path = "/can-register", produces = "application/json;charset=UTF-8")
    @CrossOrigin
    public ResponseEntity<?> register(@RequestParam String phone,
                                      @RequestHeader(value = "zuul-token", required = true)
                                              String zuulToken) {

        Boolean validateToken = zuulTokenValidator.validateToken(zuulToken);

        if (!validateToken) {
            return ResponseEntity.badRequest().body(new ResponseMessage("Zuul Token is not valid"));
        }
        return ResponseEntity.ok().body(!userService.getUser(phone).isPresent());
    }
}
