package com.gitnexus.case.consumer1.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NegativeExamplesService {

    private final RestTemplate plainRestTemplate = new RestTemplate();

    public void nonCseUrlCall() {
        plainRestTemplate.getForObject("http://localhost:18083/rest/v1/orders/1", String.class);
    }

    public void untrustedReceiverCall() {
        plainRestTemplate.getForObject("cse://order-service/rest/v1/orders/1", String.class);
    }

    public String dynamicUrlCall(String runtimeUrl) {
        RestTemplate template = new RestTemplate();
        return template.getForObject(runtimeUrl, String.class);
    }
}
