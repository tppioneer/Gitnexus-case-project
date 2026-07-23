package com.gitnexus.case.consumer2.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest/v1")
public class AmbiguousOrderController {

    @GetMapping("/ambiguous/{id}")
    public AmbiguousDTO getAmbiguous(@PathVariable String id) {
        return new AmbiguousDTO(id, "consumer2-ambiguous-" + id);
    }
}

record AmbiguousDTO(String id, String source) {}
