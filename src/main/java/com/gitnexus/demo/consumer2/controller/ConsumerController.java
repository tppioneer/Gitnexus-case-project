package com.gitnexus.demo.consumer2.controller;

import com.gitnexus.demo.consumer2.service.OrderServiceClient;
import com.gitnexus.demo.consumer2.service.OrderDTO;
import com.gitnexus.demo.consumer2.service.SearchOrderResponse;
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
