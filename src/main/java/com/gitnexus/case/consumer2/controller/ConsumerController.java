package com.gitnexus.case.consumer2.controller;

import com.gitnexus.case.consumer2.service.OrderServiceClient;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/demo")
public class ConsumerController {

    private final OrderServiceClient orderClient;

    public ConsumerController(OrderServiceClient orderClient) {
        this.orderClient = orderClient;
    }

    @GetMapping("/orders/{id}")
    public OrderDTO getOrder(@PathVariable String id) {
        return orderClient.getOrder(id);
    }

    @GetMapping("/orders/search")
    public SearchOrderResponse searchOrders(
            @RequestParam String status,
            @RequestParam(required = false) String region) {
        return orderClient.searchOrders(status, region);
    }

    @GetMapping("/orders/ambiguous/{id}")
    public String callAmbiguous(@PathVariable String id) {
        return orderClient.callAmbiguous(id);
    }
}

record OrderDTO(String id, String name, String status, Double amount) {}
record SearchOrderResponse(String status, String region, int total) {}
