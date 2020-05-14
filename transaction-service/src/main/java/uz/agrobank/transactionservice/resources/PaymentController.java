package uz.agrobank.transactionservice.resources;

import io.jsonwebtoken.Claims;
import uz.agrobank.transactionservice.domain.Card;
import uz.agrobank.transactionservice.domain.Payment;
import uz.agrobank.transactionservice.domain.PaymentP2P;
import uz.agrobank.transactionservice.domain.PaymentType;
import uz.agrobank.transactionservice.dto.P2PForm;
import uz.agrobank.transactionservice.dto.ResponseMessage;
import uz.agrobank.transactionservice.security.AuthTokenProvider;
import uz.agrobank.transactionservice.security.ZuulTokenValidator;
import uz.agrobank.transactionservice.services.AuthService;
import uz.agrobank.transactionservice.services.CardService;
import uz.agrobank.transactionservice.services.PaymentService;
import io.vavr.control.Try;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/payment")
@Slf4j
public class PaymentController {

    @Autowired
    private AuthTokenProvider tokenProvider;

    @Autowired
    private ZuulTokenValidator zuulTokenValidator;

    @Autowired
    private PaymentService userService;

    @Autowired
    private CardService cardService;

    @Autowired
    private AuthService authService;

    @PostMapping(path = "/p2p", produces = "application/json;charset=UTF-8")
    @CrossOrigin
    public ResponseEntity<?> post(@RequestBody P2PForm form,
            @RequestHeader(value = "zuul-token", required = true) String zuulToken,
            @RequestHeader(value = "auth-token", required = true) String authToken,
            @RequestHeader(value = "x-forwarded-host", required = false) String forwardedHost,
            @RequestHeader(value = "user-agent", required = false) String userAgent,
            @RequestHeader(value = "x-forwarded-prefix", required = false) String forwardedPrefix,
            @RequestHeader(value = "host", required = false) String host
    ) {

        log.info("x-forwarded-host  " + forwardedHost);
        log.info("userAgent  " + userAgent);
        log.info("forwardedPrefix  " + forwardedPrefix);
        log.info("host  " + host);

        log.info("Zuul Token " + zuulToken);
        log.info("AuthToken " + authToken);
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
                log.info("SUCCESS: User verify is Valid");

                Optional<Card> senderCard = cardService.getCardById(form.getCardId());
                if (!senderCard.isPresent())
                    return ResponseEntity.badRequest()
                            .body(new ResponseMessage("Could not find a card, bad request"));
                if (senderCard.get().getBalance().compareTo(form.getAmount()) < 0)
                    return ResponseEntity.badRequest()
                            .body(new ResponseMessage("Balance not enough"));
                Optional<Card> recipientCard = cardService.getCardById(form.getRecipientCardId());
                if (!recipientCard.isPresent())
                    return ResponseEntity.badRequest()
                            .body(new ResponseMessage("Could not find a card, bad request"));

                Card card = senderCard.get();
                card.setBalance(card.getBalance().subtract(form.getAmount()));
                cardService.updateCard(card);

                card = recipientCard.get();
                card.setBalance(card.getBalance().add(form.getAmount()));
                cardService.updateCard(card);

                Payment payment = new Payment();
                payment.setCardId(form.getCardId());
                payment.setAmount(form.getAmount());
                payment.setPaymentType(PaymentType.P2P);
                payment.setUserId(allClaimsFromToken.getSubject());
                PaymentP2P data = new PaymentP2P();
                data.setRecipientCardId(form.getRecipientCardId());
                payment.setData(data);

                Optional<Payment> addedPayment = userService.addNewPayment(payment);

                if (addedPayment.isPresent()) {
                    log.info("Transaction is added");
                    return ResponseEntity.ok().body(addedPayment);
                }
            }
            log.error("User verify is failed");
            return ResponseEntity.badRequest().body(new ResponseMessage("Could not add transaction, bad request"));
        }
        else {
            return ResponseEntity.badRequest().body(new ResponseMessage("Could not add transaction, some error"));
        }
    }

}
