package com.gitnexus.demo.provider.controller;

import com.gitnexus.demo.provider.constants.UnresolvedRoutes;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(UnresolvedRoutes.DYNAMIC_PREFIX)
public class DynamicController {

    @GetMapping("/dynamic/orders/{id}")
    public DynamicOrderDTO getDynamicOrder(@PathVariable String id) {
        return new DynamicOrderDTO(id, "Dynamic-Order-" + id);
    }
}

record DynamicOrderDTO(String id, String name) {}
