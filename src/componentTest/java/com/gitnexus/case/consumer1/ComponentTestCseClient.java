package com.gitnexus.case.consumer1;

import com.gitnexus.case.consumer1.resttemplate.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

public class ComponentTestCseClient {

    private final RestTemplate restTemplate = RestTemplateBuilder.create();

    public String callOrderService(String orderId) {
        return restTemplate.getForObject(
                "cse://order-service/rest/v1/orders/{id}",
                String.class,
                orderId
        );
    }
}
