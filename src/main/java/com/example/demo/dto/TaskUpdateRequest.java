package com.example.demo.dto;

import com.example.demo.entity.TaskStatus;

public record TaskUpdateRequest(
        String title,
        String description,
        TaskStatus status,
        Long groupId
) {}
