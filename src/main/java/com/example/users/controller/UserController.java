package com.example.users.controller;

import com.example.users.dto.UserResponse;
import com.example.users.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserResponse> report() {
        return userService.findAll();
    }
}