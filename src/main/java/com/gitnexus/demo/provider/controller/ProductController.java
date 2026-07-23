package com.gitnexus.demo.provider.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/rest/v3")
public class ProductController {

    @GetMapping("/products/{id}")
    public ProductDTO getProduct(@PathVariable String id) {
        return new ProductDTO(id, "Product-" + id, 99.99);
    }
}

record ProductDTO(String id, String name, Double price) {}
