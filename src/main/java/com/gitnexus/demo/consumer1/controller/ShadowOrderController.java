package com.gitnexus.demo.consumer1.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest/v1")
public class ShadowOrderController {

    @GetMapping("/orders/{id}")
    public ShadowOrderDTO getOrder(@PathVariable String id) {
        return new ShadowOrderDTO(id, "Shadow-Order-" + id, "SHADOW");
    }
}

record ShadowOrderDTO(String id, String name, String source) {}
