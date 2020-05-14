package uz.agrobank.cardservice.security;

import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uz.agrobank.cardservice.dto.CodeCheckForm;

import java.util.List;

@Service
@Component
@Slf4j
public class SmsValidator {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DiscoveryClient discoveryClient;

    public Boolean validateCode(String phone, String code) {
        CodeCheckForm form = new CodeCheckForm(phone, code);
        List<ServiceInstance> instances = discoveryClient.getInstances(
                "smsservice");
        if (instances.isEmpty())
            return false;

        Try<String> col = Try.of(() -> restTemplate.postForObject(instances.get(0).getUri().toString()+"/api/v2/sms/check/", form, String.class));
        if (!col.isSuccess()) {
            log.error("LABEL: Fail " + col.getCause().getMessage());
            return false;
        }
        return true;
    }

}
