package com.gitnexus.demo.provider.controller;

import com.gitnexus.demo.provider.constants.UnresolvedRoutes;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest/v1")
public class OrderController {

    @GetMapping("/orders/{id}")
    public OrderDTO getOrder(@PathVariable String id) {
        return new OrderDTO(id, "Order-" + id, "COMPLETED", 100.0);
    }

    @RequestMapping(
        path = "/orders/batch",
        method = RequestMethod.POST
    )
    public BatchOrderResponse batchOrders(@RequestBody BatchOrderRequest request) {
        return new BatchOrderResponse(request.ids().size(), "Batch processed");
    }

    @RequestMapping(
        value = "/orders/{id}",
        method = RequestMethod.DELETE
    )
    public DeleteResponse deleteOrder(@PathVariable String id) {
        return new DeleteResponse(id, true);
    }

    @PostMapping("/orders")
    public OrderDTO createOrder(@RequestBody CreateOrderRequest request) {
        return new OrderDTO("new-" + System.currentTimeMillis(), request.name(), "CREATED", request.amount());
    }

    @GetMapping("/orders/search")
    public SearchOrderResponse searchOrders(
            @RequestParam String status,
            @RequestParam(required = false) String region) {
        return new SearchOrderResponse(status, region, 5);
    }

    @GetMapping("/ambiguous/{id}")
    public AmbiguousDTO getAmbiguous(@PathVariable String id) {
        return new AmbiguousDTO(id, "provider-ambiguous-" + id);
    }
}

record OrderDTO(String id, String name, String status, Double amount) {}
record BatchOrderRequest(java.util.List<String> ids) {}
record BatchOrderResponse(int count, String message) {}
record DeleteResponse(String id, boolean deleted) {}
record CreateOrderRequest(String name, Double amount) {}
record SearchOrderResponse(String status, String region, int total) {}
record AmbiguousDTO(String id, String source) {}
