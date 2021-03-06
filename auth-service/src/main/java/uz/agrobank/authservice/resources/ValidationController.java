package uz.agrobank.authservice.resources;

import uz.agrobank.authservice.domain.User;
import uz.agrobank.authservice.dto.ResponseMessage;
import uz.agrobank.authservice.security.AuthTokenProvider;
import uz.agrobank.authservice.security.Device;
import uz.agrobank.authservice.services.UserService;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/validation")
@Slf4j
public class ValidationController {

    @Autowired
    private AuthTokenProvider tokenProvider;

    @Autowired
    private UserService userService;

    @GetMapping(path = "/verify/user/{phone}", produces = "application/json;charset=UTF-8")
    @CrossOrigin
    public ResponseEntity<?> get(@PathVariable(name = "phone", required = true) String phone,
            @RequestHeader(value = "x-forwarded-host", required = false) String forwardedHost,
            @RequestHeader(value = "user-agent", required = false) String userAgent,
            @RequestHeader(value = "x-forwarded-prefix", required = false) String forwardedPrefix,
            @RequestHeader(value = "host", required = false) String host,
            ServerHttpRequest serverHttpRequest
    ) {

        log.info("#####HOST " + serverHttpRequest.getHeaders().getHost().toString());
        log.info("#####getETag " + serverHttpRequest.getHeaders().getETag());
        log.info("#####getConnection " + serverHttpRequest.getHeaders().getConnection().toString());
        // log.info("#####getLocation " + serverHttpRequest.getHeaders().getLocation().toString());
        log.info("#####getPragma " + serverHttpRequest.getHeaders().getPragma());

        log.info("DUMP  " + serverHttpRequest.toString());
        log.info(" ****************************************** ");
        log.info("x-forwarded-host  " + forwardedHost);
        log.info("userAgent  " + userAgent);
        log.info("forwardedPrefix  " + forwardedPrefix);
        log.info("host  " + host);

        log.info("######getHostName " + serverHttpRequest.getRemoteAddress().getHostName());
        log.info("######getHostString " + serverHttpRequest.getRemoteAddress().getHostString());
        log.info("######getCanonicalHostName " + serverHttpRequest.getRemoteAddress().getAddress().getCanonicalHostName());
        log.info("######getHostAddress " + serverHttpRequest.getRemoteAddress().getAddress().getHostAddress());
        log.info("######getHostName " + serverHttpRequest.getRemoteAddress().getAddress().getHostName());
        HttpHeaders headers = serverHttpRequest.getHeaders();
        log.info("#######headers  " + headers.toString());

        Optional<User> loggedUser = userService.findByPhone(phone);

        if (loggedUser.isPresent()) {

            log.info("Verify:::: USER IS PRESENT, lets generate new token for him");

            final Device device = new Device(true, false, false);

            return ResponseEntity.ok().body(tokenProvider.generateToken(loggedUser.get().getPhone(), device));
        }

        if (!loggedUser.isPresent()) {
            log.error("Verify User is not present");
            return ResponseEntity.badRequest().body(new ResponseMessage("User does not exist"));
        }

        return ResponseEntity.notFound().build();

    }

}
