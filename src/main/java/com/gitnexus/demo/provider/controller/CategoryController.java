package com.gitnexus.demo.provider.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/rest/v4")
public class CategoryController {

    @GetMapping("/categories/{id}")
    public CategoryDTO getCategory(@PathVariable String id) {
        return new CategoryDTO(id, "Category-" + id);
    }
}

record CategoryDTO(String id, String name) {}
