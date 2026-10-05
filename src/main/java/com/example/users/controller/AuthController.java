package com.example.users.controller;

import com.example.users.dto.PasswordResetRequest;
import com.example.users.dto.RegistrationRequest;
import com.example.users.dto.UserResponse;
import com.example.users.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(
            @Valid @RequestBody RegistrationRequest request) {

        return userService.register(request);
    }

    @PostMapping("/reset-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetPassword(
            @Valid @RequestBody PasswordResetRequest request) {

        userService.resetPassword(request);
    }
}