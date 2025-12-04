package com.example.demo.controller;

import com.example.demo.dto.TaskGroupCreateRequest;
import com.example.demo.dto.TaskGroupUpdateRequest;
import com.example.demo.dto.TaskGroupResponse;
import com.example.demo.entity.TaskGroup;
import com.example.demo.service.TaskGroupService;
import com.example.demo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/api/groups")
public class TaskGroupController {

    private final TaskGroupService taskGroupService;
    private final UserService userService;

    public TaskGroupController(TaskGroupService taskGroupService, UserService userService) {
        this.taskGroupService = taskGroupService;
        this.userService = userService;
    }

    private Long getCurrentUserId(Authentication authentication) {
        String email = authentication.getName();
        return userService.getUserByEmail(email).getId();
    }

    @Operation(summary = "Получить список своих групп задач")
    @GetMapping
    public List<TaskGroupResponse> getUserGroups(Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        return taskGroupService.getUserGroups(userId)
                .stream()
                .map(g -> new TaskGroupResponse(g.getId(), g.getName()))
                .collect(Collectors.toList());
    }

    @Operation(summary = "Создать новую группу задач")
    @PostMapping
    public ResponseEntity<TaskGroupResponse> createGroup(@RequestBody TaskGroupCreateRequest request,
                                                         Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        TaskGroup group = taskGroupService.createGroup(request.name(), userId);
        return ResponseEntity.ok(new TaskGroupResponse(group.getId(), group.getName()));
    }

    @Operation(summary = "Переименовать группу задач")
    @PutMapping("/{id}")
    public ResponseEntity<TaskGroupResponse> updateGroup(@PathVariable Long id,
                                                         @RequestBody TaskGroupUpdateRequest request,
                                                         Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        TaskGroup group = taskGroupService.updateGroup(id, request.name(), userId);
        return ResponseEntity.ok(new TaskGroupResponse(group.getId(), group.getName()));
    }

    @Operation(summary = "Удалить группу задач")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long id,
                                            Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        taskGroupService.deleteGroup(id, userId);
        return ResponseEntity.noContent().build();
    }
}
