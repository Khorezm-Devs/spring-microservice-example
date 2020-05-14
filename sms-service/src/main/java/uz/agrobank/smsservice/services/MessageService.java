package uz.agrobank.smsservice.services;

import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uz.agrobank.smsservice.domain.Message;
import uz.agrobank.smsservice.dto.PlayMobileMessageDTO;
import uz.agrobank.smsservice.dto.PlayMobileRequest;
import uz.agrobank.smsservice.dto.PlayMobileSmsContentDTO;
import uz.agrobank.smsservice.dto.PlayMobileSmsDTO;
import uz.agrobank.smsservice.repositories.MessageRepository;

import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
@Component
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private RestTemplate restTemplate;

    final String PLAY_MOBILE_URL = "HERE_WAS_URL_OF_PLAYMOBILE_API";
    final String PLAY_MOBILE_LOGIN = "blahblah";
    final String PLAY_MOBILE_PASSWORD = "blahblahblah";

    public Optional<List<Message>> findByPhone(String phone) {
        return messageRepository.findByPhone(phone);
    }

    public Optional<Message> findByPhoneAndCode(String phone, String code) {
        return messageRepository.findByPhoneAndCode(phone, code);
    }

    public Optional<Message> findLastMessageByPhone(String phone){
        return messageRepository.findTopByPhoneOrderByCreatedAtDesc(phone);
    }

    public Message createMessage(Message message) {

        return messageRepository.save(message);
    }

    public void sendMessage(String phoneNum, String code, String id){
        List<PlayMobileMessageDTO> messages = new LinkedList<>();
        PlayMobileSmsDTO smsDTO = new PlayMobileSmsDTO();
        smsDTO.setContent(new PlayMobileSmsContentDTO("Kod dlya logina: " + code));
        smsDTO.setOriginator("2300");
        messages.add(new PlayMobileMessageDTO(phoneNum, id, smsDTO));
        PlayMobileRequest request = new PlayMobileRequest(messages);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(HttpHeaders.AUTHORIZATION, "Basic " + new String(Base64.getEncoder()
                .encode((PLAY_MOBILE_LOGIN + ":" + PLAY_MOBILE_PASSWORD).getBytes())));
        HttpEntity<PlayMobileRequest> entity = new HttpEntity<>(request, headers);
        Try<String> result = Try.of(() -> restTemplate.postForObject(PLAY_MOBILE_URL, entity, String.class));
//        if (result.isSuccess())
//            return true;
//        return false;

    }

}
