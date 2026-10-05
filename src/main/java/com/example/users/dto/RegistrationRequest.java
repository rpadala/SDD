package com.example.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record RegistrationRequest(

        @NotBlank String firstName,

        @NotBlank String lastName,

        @NotBlank @Email String email,

        @NotBlank String password,

        String address,

        LocalDate dateOfBirth) {
}