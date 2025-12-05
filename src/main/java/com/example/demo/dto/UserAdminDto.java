package com.example.demo.dto;

public record UserAdminDto(
        Long id,
        String name,
        String email,
        String role
) {
}
