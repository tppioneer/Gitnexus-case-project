package com.gitnexus.case.consumer1.service;

import com.gitnexus.case.consumer1.resttemplate.RestTemplateBuilder;
import com.gitnexus.case.contracts.PlatformRoutes;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OrderServiceClient {

    private final RestTemplate restTemplate = RestTemplateBuilder.create();

    public OrderDTO getOrder(String id) {
        String url = PlatformRoutes.ORDER_SERVICE + PlatformRoutes.ORDER_PREFIX + "/{id}";
        return restTemplate.getForObject(url, OrderDTO.class, id);
    }

    public BatchOrderResponse batchOrders(java.util.List<String> ids) {
        String url = PlatformRoutes.ORDER_SERVICE + PlatformRoutes.ORDER_PREFIX + "/batch";
        BatchOrderRequest request = new BatchOrderRequest(ids);
        return restTemplate.postForObject(url, request, BatchOrderResponse.class);
    }

    public DeleteResponse deleteOrder(String id) {
        String url = PlatformRoutes.ORDER_SERVICE + PlatformRoutes.ORDER_PREFIX + "/{id}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.DELETE, entity, DeleteResponse.class, id).getBody();
    }

    public SearchOrderResponse searchOrders(String status, String region) {
        String url = PlatformRoutes.ORDER_SERVICE + PlatformRoutes.ORDER_PREFIX + "/search?status={status}";
        if (region != null) {
            url += "&region={region}";
            return restTemplate.getForObject(url, SearchOrderResponse.class, status, region);
        }
        return restTemplate.getForObject(url, SearchOrderResponse.class, status);
    }

    public OrderDTO createOrder(String name, Double amount) {
        String url = PlatformRoutes.ORDER_SERVICE + PlatformRoutes.ORDER_PREFIX;
        CreateOrderRequest request = new CreateOrderRequest(name, amount);
        return restTemplate.postForObject(url, request, OrderDTO.class);
    }

    public String getAmbiguous(String id) {
        String url = PlatformRoutes.ORDER_SERVICE + PlatformRoutes.ORDER_PREFIX.replace("/orders", "") + "/ambiguous/{id}";
        AmbiguousDTO result = restTemplate.getForObject(url, AmbiguousDTO.class, id);
        return result != null ? result.source() : null;
    }
}

record OrderDTO(String id, String name, String status, Double amount) {}
record BatchOrderRequest(java.util.List<String> ids) {}
record BatchOrderResponse(int count, String message) {}
record DeleteResponse(String id, boolean deleted) {}
record CreateOrderRequest(String name, Double amount) {}
record SearchOrderResponse(String status, String region, int total) {}
record AmbiguousDTO(String id, String source) {}
