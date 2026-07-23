package com.gitnexus.case.consumer2.service;

import com.gitnexus.case.consumer2.resttemplate.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OrderServiceClient {

    private static final String ORDER_SERVICE = "cse://order-service";
    private static final String API_PREFIX = "/rest/v1";

    private final RestTemplate restTemplate = RestTemplateBuilder.create();

    public OrderDTO getOrder(String id) {
        String url = ORDER_SERVICE + API_PREFIX + "/orders/{id}";
        return restTemplate.getForObject(url, OrderDTO.class, id);
    }

    public SearchOrderResponse searchOrders(String status, String region) {
        String url = ORDER_SERVICE + API_PREFIX + "/orders/search?status={status}";
        if (region != null) {
            url += "&region={region}";
            return restTemplate.getForObject(url, SearchOrderResponse.class, status, region);
        }
        return restTemplate.getForObject(url, SearchOrderResponse.class, status);
    }

    public String callAmbiguous(String id) {
        String url = ORDER_SERVICE + API_PREFIX + "/ambiguous/{id}";
        AmbiguousDTO result = restTemplate.getForObject(url, AmbiguousDTO.class, id);
        return result != null ? result.source() : null;
    }
}

record OrderDTO(String id, String name, String status, Double amount) {}
record SearchOrderResponse(String status, String region, int total) {}
record AmbiguousDTO(String id, String source) {}
