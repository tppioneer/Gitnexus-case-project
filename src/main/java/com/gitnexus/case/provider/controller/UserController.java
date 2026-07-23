package com.gitnexus.case.provider.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest/v2")
public class UserController {

    @GetMapping("/users/{id}")
    public UserDTO getUser(@PathVariable String id) {
        return new UserDTO(id, "User-" + id, "ACTIVE");
    }

    @PostMapping("/users")
    public UserDTO createUser(@RequestBody CreateUserRequest request) {
        return new UserDTO("new-" + System.currentTimeMillis(), request.name(), "ACTIVE");
    }
}

record UserDTO(String id, String name, String status) {}
record CreateUserRequest(String name) {}
