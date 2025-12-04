package com.example.demo.dto;

import com.example.demo.entity.TaskStatus;

public record TaskResponse(
        Long id,
        String title,
        String description,
        TaskStatus status,
        Long groupId
) {}
