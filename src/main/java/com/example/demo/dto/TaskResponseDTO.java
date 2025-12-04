package com.example.demo.dto;

import com.example.demo.entity.TaskStatus;

public record TaskResponseDTO(
        Long id,
        String title,
        String description,
        TaskStatus status,
        TaskGroupResponseDTO group,
        UserResponse user
) {
}