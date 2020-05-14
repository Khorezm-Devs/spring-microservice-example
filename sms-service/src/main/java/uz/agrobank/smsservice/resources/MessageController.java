package uz.agrobank.smsservice.resources;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.agrobank.smsservice.domain.Message;
import uz.agrobank.smsservice.dto.CodeCheckForm;
import uz.agrobank.smsservice.dto.ResponseMessage;
import uz.agrobank.smsservice.security.ZuulTokenValidator;
import uz.agrobank.smsservice.services.MessageService;

import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/api/v2/sms")
@Slf4j
public class MessageController {

    @Autowired
    private ZuulTokenValidator zuulTokenValidator;

    @Autowired
    private MessageService messageService;

    @GetMapping(path = "/request", produces = "application/json;charset=UTF-8")
    @CrossOrigin
    public ResponseEntity<?> request(
            @RequestParam String phone,
            @RequestHeader(value = "zuul-token", required = true) String zuulToken) {

        Boolean validateToken = zuulTokenValidator.validateToken(zuulToken);
        if (!validateToken) {
            return ResponseEntity.badRequest().body(new ResponseMessage("Zuul Token is not valid"));
        }

        String code = String.valueOf((10000 + new Random().nextInt(90000)));

        Optional<Message> message = Optional.ofNullable(messageService.createMessage(new Message(phone, code)));

        if (message.isPresent()) {
            messageService.sendMessage(phone, code, message.get().getId());
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping(path = "/check", produces = "application/json;charset=UTF-8")
    @CrossOrigin
    public ResponseEntity<?> check(
            @RequestBody CodeCheckForm form,
            @RequestHeader(value = "zuul-token", required=false) String zuulToken) {

        if (zuulToken != null && !zuulToken.equals("")) {
            return ResponseEntity.badRequest().body(new ResponseMessage("Zuul Token is not valid"));
        }
        Optional<Message> message = messageService.findLastMessageByPhone(form.getPhone());
        if (message.isPresent()
            && message.get().getCode().equals(form.getCode()))
            return ResponseEntity.ok().build();

        return ResponseEntity.badRequest().build();

    }
}
