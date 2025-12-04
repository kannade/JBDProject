package com.example.demo.controller;

import com.example.demo.dto.AuthRequest;
import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Регистрация нового пользователя")
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterRequest request) {
        User user = userService.register(request.name(), request.email(), request.password(), request.role());
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Авторизация пользователя (возвращает JWT)")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Parameter(description = "Данные для авторизации") @RequestBody AuthRequest request) {
        AuthResponse response = userService.login(request.email(), request.password());
        return ResponseEntity.ok(response);
    }
}


