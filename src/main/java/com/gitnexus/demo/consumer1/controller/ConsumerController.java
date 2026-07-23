package com.gitnexus.demo.consumer1.controller;

import com.gitnexus.demo.consumer1.service.NegativeExamplesService;
import com.gitnexus.demo.consumer1.service.OrderServiceClient;
import com.gitnexus.demo.consumer1.service.OrderDTO;
import com.gitnexus.demo.consumer1.service.BatchOrderResponse;
import com.gitnexus.demo.consumer1.service.DeleteResponse;
import com.gitnexus.demo.consumer1.service.SearchOrderResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/demo")
public class ConsumerController {

    private final OrderServiceClient orderClient;
    private final NegativeExamplesService negativeExamples;

    public ConsumerController(OrderServiceClient orderClient, NegativeExamplesService negativeExamples) {
        this.orderClient = orderClient;
        this.negativeExamples = negativeExamples;
    }

    @GetMapping("/orders/{id}")
    public OrderDTO getOrder(@PathVariable String id) {
        return orderClient.getOrder(id);
    }

    @PostMapping("/orders/batch")
    public BatchOrderResponse batchOrders(@RequestBody List<String> ids) {
        return orderClient.batchOrders(ids);
    }

    @DeleteMapping("/orders/{id}")
    public DeleteResponse deleteOrder(@PathVariable String id) {
        return orderClient.deleteOrder(id);
    }

    @GetMapping("/orders/search")
    public SearchOrderResponse searchOrders(
            @RequestParam String status,
            @RequestParam(required = false) String region) {
        return orderClient.searchOrders(status, region);
    }

    @PostMapping("/orders")
    public OrderDTO createOrder(@RequestBody CreateOrderRequest request) {
        return orderClient.createOrder(request.name(), request.amount());
    }

    @GetMapping("/orders/ambiguous/{id}")
    public String getAmbiguous(@PathVariable String id) {
        return orderClient.getAmbiguous(id);
    }

    @GetMapping("/negative/non-cse")
    public void nonCseUrlDemo() {
        negativeExamples.nonCseUrlCall();
    }

    @GetMapping("/negative/untrusted")
    public void untrustedReceiverDemo() {
        negativeExamples.untrustedReceiverCall();
    }
}

record CreateOrderRequest(String name, Double amount) {}
