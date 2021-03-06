package uz.agrobank.gateway.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import uz.agrobank.gateway.dto.Claim;
import uz.agrobank.gateway.security.AuthTokenProvider;
import uz.agrobank.gateway.security.Device;
import uz.agrobank.gateway.security.ZuulTokenProvider;
import uz.agrobank.gateway.services.AuthService;
import io.vavr.control.Try;

import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class RequestFilter extends ZuulFilter {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AuthTokenProvider tokenProvider;

    @Autowired
    private ZuulTokenProvider zuulTokenProvider;

    @Autowired
    private AuthService authService;

    private static final int FILTER_ORDER = 1;
    private static final boolean SHOULD_FILTER = true;

    @Override
    public Object run() {

        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String userAgent = request.getHeader("User-Agent");
        String authToken = request.getHeader("AUTH-TOKEN");
        log.info("ZUUL report: User-Agent is " + userAgent);
        log.info("ZUUL report: AuthToken is " + authToken);

//        if (ctx.getRequest().getRequestURI().contains("actuator")) {
//            log.info("ACTUATOR BLOCKED");
//            ctx.setResponseStatusCode(401);
//            ctx.setSendZuulResponse(false);
//            ctx.setResponseBody("Actuator? Dear user your requestes url does not exist any more.");
//            return null;
//        }

//        if (ctx.getRequest().getRequestURI().contains("swagger")) {
//            log.info("SWAGGER BLOCKED");
//            ctx.setResponseStatusCode(401);
//            ctx.setSendZuulResponse(false);
//            ctx.setResponseBody("Swagger? Dear user your requestes url does not exist any more.");
//            return null;
//        }

        log.info("Request method " + ctx.getRequest().getMethod());
        if (!ctx.getRequest().getMethod().equalsIgnoreCase("OPTIONS")) {
            log.info("Request method " + ctx.getRequest().getMethod());

            if (ctx.getRequest().getRequestURI().contains("auth") || ctx.getRequest().getRequestURI().contains("sms")
                    || ctx.getRequest().getRequestURI().contains("register")) {
                log.info("Processing incoming request for {}.", ctx.getRequest().getRequestURI());
                ctx.getResponse().addHeader(FilterUtils.ZUUL_TOKEN, zuulTokenProvider.generateToken("zuultoken", new Device(true, false, false)));
                ctx.addZuulRequestHeader(FilterUtils.ZUUL_TOKEN, zuulTokenProvider.generateToken("zuultoken", new Device(true, false, false)));
            }

            if (!ctx.getRequest().getRequestURI().contains("auth") && !ctx.getRequest().getRequestURI().contains("sms")
                    && !ctx.getRequest().getRequestURI().contains("register")) {

                if (authToken == null) {
                    log.info("Auth token is null");
                    ctx.setResponseStatusCode(401);
                    ctx.setSendZuulResponse(false);
                    ctx.setResponseBody("Auth Token does not exist");
                    return null;
                }

                log.info("Validating token locally");
                Boolean validateToken = tokenProvider.validateToken(authToken);

                if (!validateToken) {
                    ctx.setResponseStatusCode(401);
                    ctx.setSendZuulResponse(false);
                    ctx.setResponseBody("Auth Token invalid, stop proccessing");
                    return null;
                }

                Try<Claim> verifyToken = authService.verifyToken(authToken);

                if (verifyToken.isSuccess()) {
                    log.info("This can check token which is stored in database");
                    log.info("*************ZUUL REQUEST  START ********************");
                    log.info("Processing incoming request for {}.", ctx.getRequest().getRequestURI());
                    ctx.getResponse().addHeader(FilterUtils.ZUUL_TOKEN, zuulTokenProvider.generateToken("zuultoken", new Device(true, false, false)));
                    ctx.addZuulRequestHeader(FilterUtils.ZUUL_TOKEN, zuulTokenProvider.generateToken("zuultoken", new Device(true, false, false)));
                    log.info(String.format("%s request to %s", request.getMethod(), request.getRequestURL().toString()));
                    log.info("*************ZUUL REQUEST FINISH ********************");
                }

                if (!verifyToken.isSuccess()) {
                    ctx.setResponseStatusCode(401);
                    ctx.setSendZuulResponse(false);
                    ctx.setResponseBody("Auth Token invalid");
                    return null;
                }
            }
        }

        return null;
    }

    @Override
    public String filterType() {
        return FilterUtils.PRE_FILTER_TYPE;
    }

    @Override
    public int filterOrder() {
        return FILTER_ORDER;
    }

    @Override
    public boolean shouldFilter() {
        return SHOULD_FILTER;
    }

    public String traceClaim(Claim claim) {
        return claim.toString();
    }
}
