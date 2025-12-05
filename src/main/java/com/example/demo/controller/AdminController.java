package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.entity.TaskStatus;
import com.example.demo.entity.User;
import com.example.demo.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @Operation(summary = "Получить список всех пользователей")
    @GetMapping("/users")
    public List<UserResponse> getAllUsers() {
        return adminService.getAllUsers().stream()
                .map(u -> new UserResponse(
                        u.getId(),
                        u.getName(),
                        u.getEmail(),
                        u.getRole()
                ))
                .toList();
    }

    @Operation(summary = "Получить список всех задач")
    @GetMapping("/tasks")
    public List<TaskResponseDTO> getAllTasks() {
        return adminService.getAllTasks().stream()
                .map(t -> new TaskResponseDTO(
                        t.getId(),
                        t.getTitle(),
                        t.getDescription(),
                        t.getStatus(),
                        t.getGroup() != null ? new TaskGroupResponseDTO(t.getGroup().getId(), t.getGroup().getName()) : null,
                        t.getUser() != null ? new UserResponse(t.getUser().getId(), t.getUser().getName(), t.getUser().getEmail(), t.getUser().getRole()) : null
                ))
                .toList();
    }

    @Operation(summary = "Получить список всех групп задач")
    @GetMapping("/groups")
    public List<TaskGroupResponseDTO> getAllGroups() {
        return adminService.getAllGroups().stream()
                .map(g -> new TaskGroupResponseDTO(
                        g.getId(),
                        g.getName()
                ))
                .toList();
    }

    @Operation(summary = "Получить задачи конкретного пользователя")
    @GetMapping("/users/{userId}/tasks")
    public List<TaskResponseDTO> getTasksByUser(@PathVariable Long userId) {
        return adminService.getTasksByUser(userId).stream()
                .map(t -> new TaskResponseDTO(
                        t.getId(),
                        t.getTitle(),
                        t.getDescription(),
                        t.getStatus(),
                        t.getGroup() != null ? new TaskGroupResponseDTO(t.getGroup().getId(), t.getGroup().getName()) : null,
                        null
                ))
                .toList();
    }

    @Operation(summary = "Получить статистику задач по статусам")
    @GetMapping("/tasks/statistics/status")
    public Map<TaskStatus, Long> getTaskCountByStatus() {
        return adminService.getTaskCountByStatus();
    }

    @Operation(summary = "Получить статистику задач по группам")
    @GetMapping("/tasks/statistics/group")
    public Map<String, Long> getTaskCountByGroup() {
        return adminService.getTaskCountByGroup();
    }

    @Operation(summary = "Поиск пользователей по имени (пример SQL-запроса)")
    @GetMapping("/users/search")
    public List<UserAdminDto> searchUsers(
            @RequestParam String name
    ) {
        return adminService.searchUsers(name);
    }
}
