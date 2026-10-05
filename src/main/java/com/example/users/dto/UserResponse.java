package com.example.users.dto;

import java.time.LocalDate;

public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String address,
        LocalDate dateOfBirth) {
}