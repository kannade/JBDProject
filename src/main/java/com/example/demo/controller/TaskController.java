package com.example.demo.controller;

import com.example.demo.dto.TaskCreateRequest;
import com.example.demo.dto.TaskUpdateRequest;
import com.example.demo.dto.TaskResponse;
import com.example.demo.entity.Task;
import com.example.demo.entity.TaskGroup;
import com.example.demo.entity.TaskStatus;
import com.example.demo.service.TaskService;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;

    public TaskController(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
    }

    private Long getCurrentUserId(Authentication authentication) {
        String email = authentication.getName();
        return userService.getUserByEmail(email).getId();
    }

    @Operation(summary = "Получить список своих задач")
    @GetMapping
    public List<TaskResponse> getUserTasks(Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        return taskService.getUserTasks(userId)
                .stream()
                .map(task -> new TaskResponse(
                        task.getId(),
                        task.getTitle(),
                        task.getDescription(),
                        task.getStatus(),
                        task.getGroup() != null ? task.getGroup().getId() : null
                ))
                .collect(Collectors.toList());
    }

    @Operation(summary = "Создать новую задачу")
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @Parameter(description = "Данные создаваемой задачи") @RequestBody TaskCreateRequest request,
            Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        Task task = new Task();
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setStatus(request.status());

        Task created = taskService.createTask(task, userId);

        if (request.groupId() != null) {
            taskService.changeTaskGroup(created.getId(), request.groupId(), userId);
        }

        TaskResponse response = new TaskResponse(
                created.getId(),
                created.getTitle(),
                created.getDescription(),
                created.getStatus(),
                created.getGroup() != null ? created.getGroup().getId() : null
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Обновить задачу")
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @Parameter(description = "ID задачи") @PathVariable Long id,
            @Parameter(description = "Новые данные задачи") @RequestBody TaskUpdateRequest request,
            Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        Task newData = new Task();
        newData.setTitle(request.title());
        newData.setDescription(request.description());
        newData.setStatus(request.status());

        if (request.groupId() != null) {
            TaskGroup group = new TaskGroup();
            group.setId(request.groupId());
            newData.setGroup(group);
        }

        Task updated = taskService.updateTask(id, newData, userId);

        TaskResponse response = new TaskResponse(
                updated.getId(),
                updated.getTitle(),
                updated.getDescription(),
                updated.getStatus(),
                updated.getGroup() != null ? updated.getGroup().getId() : null
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Удалить задачу")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @Parameter(description = "ID задачи") @PathVariable Long id,
            Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        taskService.deleteTask(id, userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Изменить статус задачи")
    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponse> updateStatus(
            @Parameter(description = "ID задачи") @PathVariable Long id,
            @Parameter(description = "Новый статус задачи") @RequestParam("status") TaskStatus status,
            Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        Task updated = taskService.updateStatus(id, status, userId);

        TaskResponse response = new TaskResponse(
                updated.getId(),
                updated.getTitle(),
                updated.getDescription(),
                updated.getStatus(),
                updated.getGroup() != null ? updated.getGroup().getId() : null
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Изменить группу задачи")
    @PatchMapping("/{id}/group")
    public ResponseEntity<TaskResponse> changeGroup(
            @Parameter(description = "ID задачи") @PathVariable Long id,
            @Parameter(description = "ID новой группы") @RequestParam("groupId") Long groupId,
            Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        Task updated = taskService.changeTaskGroup(id, groupId, userId);

        TaskResponse response = new TaskResponse(
                updated.getId(),
                updated.getTitle(),
                updated.getDescription(),
                updated.getStatus(),
                updated.getGroup() != null ? updated.getGroup().getId() : null
        );

        return ResponseEntity.ok(response);
    }
}
