package com.gitnexus.case.consumer2.resttemplate;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RestTemplateBuilder {

    private static Map<String, String> serviceMappings = Map.of(
            "order-service", "http://localhost:18083"
    );

    public static RestTemplate create() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(5000);

        CseUriTemplateHandler handler = new CseUriTemplateHandler(serviceMappings);

        RestTemplate restTemplate = new RestTemplate(factory);

        List<org.springframework.http.client.ClientHttpRequestInterceptor> interceptors =
                new ArrayList<>(restTemplate.getInterceptors());
        interceptors.add(handler);
        restTemplate.setInterceptors(interceptors);

        return restTemplate;
    }

    public static void setServiceMappings(Map<String, String> mappings) {
        serviceMappings = mappings;
    }

    public static Map<String, String> getServiceMappings() {
        return serviceMappings;
    }
}
