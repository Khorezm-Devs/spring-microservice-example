package uz.agrobank.cardservice.services;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Component
@Slf4j
public class AuthService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private EurekaClient discoveryClient;

    public Try<String> verifyUser(String phone) {
        log.info("LABEL: verifyUser: email " + phone);
        String homePage = this.serviceUrl("authservice");
        Try<String> col = Try.of(() -> restTemplate.getForObject(homePage + "api/v2/validation/verify/user/{phone}", String.class, phone));
        if (!col.isSuccess()) {
            log.error("LABEL: verifyUser Fail " + phone);
            return Try.failure(col.getCause());
        }

        if (col.isSuccess()) {
            log.info("LABEL: verifyUser SUCCESS FOR  " + phone);
        }
        return col;
    }

    public String serviceUrl(String service) {
        InstanceInfo instance = discoveryClient.getNextServerFromEureka(service, false);
        log.info("HOME PAGE URL " + instance.getHomePageUrl());
        return instance.getHomePageUrl();
    }

}
