package com.example.demo.dto;

import com.example.demo.entity.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateUserRequest(
        @NotBlank String name,
        @NotBlank String email,
        @NotNull Role role
) {
}
