package uz.agrobank.cardservice.resources;

import io.jsonwebtoken.Claims;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.agrobank.cardservice.domain.Card;
import uz.agrobank.cardservice.dto.CardCreateForm;
import uz.agrobank.cardservice.dto.CardUpdateForm;
import uz.agrobank.cardservice.dto.ResponseMessage;
import uz.agrobank.cardservice.security.SmsValidator;
import uz.agrobank.cardservice.security.TokenProvider;
import uz.agrobank.cardservice.security.ZuulTokenValidator;
import uz.agrobank.cardservice.services.AuthService;
import uz.agrobank.cardservice.services.CardService;

import java.math.BigInteger;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/api/v2/card")
@Slf4j
public class CardController {

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private ZuulTokenValidator zuulTokenValidator;

    @Autowired
    private AuthService authService;

    @Autowired
    private CardService cardService;

    @Autowired
    private SmsValidator smsValidator;

    @PostMapping(path = "/register", produces = "application/json;charset=UTF-8")
    @CrossOrigin
    public ResponseEntity<?> registerCard(
            @RequestBody CardCreateForm cardCreateForm,
            @RequestHeader(value = "zuul-token", required = true) String zuulToken,
            @RequestHeader(value = "auth-token", required = true) String authToken
    ) {

        Boolean authTokenValidation = tokenProvider.validateToken(authToken);

        if (!authTokenValidation) {
            return ResponseEntity.badRequest().body(new ResponseMessage("Auth Token is not valid"));
        }

        Boolean validateToken = zuulTokenValidator.validateToken(zuulToken);

        if (!validateToken) {
            return ResponseEntity.badRequest().body(new ResponseMessage("Zuul Token is not valid"));
        }

        Claims allClaimsFromToken = tokenProvider.getAllClaimsFromToken(authToken);
        if (allClaimsFromToken != null && allClaimsFromToken.getSubject() != null) {

            Try<String> verifyUser = authService.verifyUser(allClaimsFromToken.getSubject());
            if (verifyUser.isSuccess()) {
                log.info("SUCCESS: User verify by phone is Valid");
                Boolean validateSms = smsValidator.validateCode(allClaimsFromToken.getSubject(), cardCreateForm.getVerification());
                if (!validateSms) {
                    return ResponseEntity.badRequest().body(new ResponseMessage("Verification code is not valid"));
                }
                Card card = new Card();
                card.setPan(cardCreateForm.getCardNumber());
                card.setExpiry(cardCreateForm.getExpireDate());
                Random random = new Random();
                card.setBalance(BigInteger.valueOf(random.nextInt(1000000000) + 1000000000));
                card.setPhone(allClaimsFromToken.getSubject());
                card.setUserId(allClaimsFromToken.getSubject());
                card.setLocalCardName(cardCreateForm.getCardName());
                Optional<Card> insertedCard = cardService.insertCard(card);
                if (insertedCard.isPresent())
                    return ResponseEntity.ok(insertedCard);
                return ResponseEntity.badRequest().body(new ResponseMessage("Could not register new card"));
            }

            if (verifyUser.isFailure()) {
                log.error("User verify by email is failed");
                return ResponseEntity.badRequest().body(new ResponseMessage("Could not add wiki, bad request"));
            }

        }

        return ResponseEntity.badRequest().body(new ResponseMessage("Could not register new card"));

    }

    @GetMapping(path = "/my", produces = "application/json;charset=UTF-8")
    @CrossOrigin
    public ResponseEntity<?> my(
            @RequestHeader(value = "zuul-token", required = true) String zuulToken,
            @RequestHeader(value = "auth-token", required = true) String authToken
    ) {

        Boolean authTokenValidation = tokenProvider.validateToken(authToken);

        if (!authTokenValidation) {
            return ResponseEntity.badRequest().body(new ResponseMessage("Auth Token is not valid"));
        }

        Boolean validateToken = zuulTokenValidator.validateToken(zuulToken);

        if (!validateToken) {
            return ResponseEntity.badRequest().body(new ResponseMessage("Zuul Token is not valid"));
        }

        Claims allClaimsFromToken = tokenProvider.getAllClaimsFromToken(authToken);
        if (allClaimsFromToken != null && allClaimsFromToken.getSubject() != null) {

            Try<String> verifyUser = authService.verifyUser(allClaimsFromToken.getSubject());

            if (verifyUser.isSuccess()) {
                log.info("SUCCESS: User verify by phone is Valid");
                return ResponseEntity.ok(cardService.getUserCards(allClaimsFromToken.getSubject()));
            }

            if (verifyUser.isFailure()) {
                log.error("User verify by email is failed");
                return ResponseEntity.badRequest().body(new ResponseMessage("Could not add wiki, bad request"));
            }

        }

        return ResponseEntity.badRequest().body(new ResponseMessage("Could not register new card"));

    }


    @GetMapping(path = "/check", produces = "application/json;charset=UTF-8")
    @CrossOrigin
    public ResponseEntity<?> check(
            @RequestParam String pan,
            @RequestHeader(value = "zuul-token", required = true) String zuulToken,
            @RequestHeader(value = "auth-token", required = true) String authToken,
            @RequestHeader(value = "x-forwarded-host", required = false) String forwardedHost,
            @RequestHeader(value = "user-agent", required = false) String userAgent,
            @RequestHeader(value = "x-forwarded-prefix", required = false) String forwardedPrefix,
            @RequestHeader(value = "host", required = false) String host
    ) {

        Boolean authTokenValidation = tokenProvider.validateToken(authToken);

        if (!authTokenValidation) {
            return ResponseEntity.badRequest().body(new ResponseMessage("Auth Token is not valid"));
        }

        Boolean validateToken = zuulTokenValidator.validateToken(zuulToken);

        if (!validateToken) {
            return ResponseEntity.badRequest().body(new ResponseMessage("Zuul Token is not valid"));
        }

        Claims allClaimsFromToken = tokenProvider.getAllClaimsFromToken(authToken);
        if (allClaimsFromToken != null && allClaimsFromToken.getSubject() != null) {

            Try<String> verifyUser = authService.verifyUser(allClaimsFromToken.getSubject());

            if (verifyUser.isSuccess()) {
                Optional<Card> card = cardService.getByPan(pan);
                if (card.isPresent())
                    return ResponseEntity.ok(card);
                return ResponseEntity.badRequest().body(new ResponseMessage("Could not find card by pan, bad request"));
            }

            if (verifyUser.isFailure()) {
                log.error("User verify by email is failed");
                return ResponseEntity.badRequest().body(new ResponseMessage("Could not add wiki, bad request"));
            }

        }

        return ResponseEntity.badRequest().body(new ResponseMessage("Could not register new card"));

    }

    @GetMapping(path = "/update", produces = "application/json;charset=UTF-8")
    @CrossOrigin
    public ResponseEntity<?> update(
            @RequestParam CardUpdateForm form,
            @RequestHeader(value = "zuul-token", required = true) String zuulToken,
            @RequestHeader(value = "auth-token", required = true) String authToken
    ) {

        Boolean authTokenValidation = tokenProvider.validateToken(authToken);

        if (!authTokenValidation) {
            return ResponseEntity.badRequest().body(new ResponseMessage("Auth Token is not valid"));
        }

        Boolean validateToken = zuulTokenValidator.validateToken(zuulToken);

        if (!validateToken) {
            return ResponseEntity.badRequest().body(new ResponseMessage("Zuul Token is not valid"));
        }

        Claims allClaimsFromToken = tokenProvider.getAllClaimsFromToken(authToken);
        if (allClaimsFromToken != null && allClaimsFromToken.getSubject() != null) {

            Try<String> verifyUser = authService.verifyUser(allClaimsFromToken.getSubject());

            if (verifyUser.isSuccess()) {
                Optional<Card> card = cardService.getCardById(form.getCardId());
                if (card.isPresent() && card.get().getUserId().equals(allClaimsFromToken.getSubject())) {
                    Card updatedCard = card.get();
                    updatedCard.setLocalCardName(form.getCardName());
                    cardService.updateCard(updatedCard);
                    return ResponseEntity.ok(updatedCard);
                }
                return ResponseEntity.badRequest().body(new ResponseMessage("Could not find card by pan, bad request"));
            }

            if (verifyUser.isFailure()) {
                log.error("User verify by email is failed");
                return ResponseEntity.badRequest().body(new ResponseMessage("Could not add wiki, bad request"));
            }

        }

        return ResponseEntity.badRequest().body(new ResponseMessage("Could not register new card"));

    }

    @GetMapping(path = "/delete", produces = "application/json;charset=UTF-8")
    @CrossOrigin
    public ResponseEntity<?> delete(
            @RequestParam String cardId,
            @RequestHeader(value = "zuul-token", required = true) String zuulToken,
            @RequestHeader(value = "auth-token", required = true) String authToken
    ) {

        Boolean authTokenValidation = tokenProvider.validateToken(authToken);

        if (!authTokenValidation) {
            return ResponseEntity.badRequest().body(new ResponseMessage("Auth Token is not valid"));
        }

        Boolean validateToken = zuulTokenValidator.validateToken(zuulToken);

        if (!validateToken) {
            return ResponseEntity.badRequest().body(new ResponseMessage("Zuul Token is not valid"));
        }

        Claims allClaimsFromToken = tokenProvider.getAllClaimsFromToken(authToken);
        if (allClaimsFromToken != null && allClaimsFromToken.getSubject() != null) {

            Try<String> verifyUser = authService.verifyUser(allClaimsFromToken.getSubject());

            if (verifyUser.isSuccess()) {
                Optional<Card> card = cardService.getCardById(cardId);
                if (card.isPresent() && card.get().getUserId().equals(allClaimsFromToken.getSubject())) {
                    Card deletedCard = card.get();
                    cardService.deleteCard(deletedCard);
                    return ResponseEntity.ok().build();
                }
                return ResponseEntity.badRequest().body(new ResponseMessage("Could not find card by pan, bad request"));
            }

            if (verifyUser.isFailure()) {
                log.error("User verify by email is failed");
                return ResponseEntity.badRequest().body(new ResponseMessage("Could not add wiki, bad request"));
            }

        }

        return ResponseEntity.badRequest().body(new ResponseMessage("Could not register new card"));

    }
}
