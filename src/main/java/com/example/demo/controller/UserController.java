package com.example.demo.controller;

import com.example.demo.dto.CreateUserRequest;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Создать нового пользователя")
    @PostMapping
    public User createUser(
            @Parameter(description = "Данные нового пользователя")
            @Valid @RequestBody CreateUserRequest request) {
        return userService.createUser(request.name(), request.email(), request.role());
    }

    @Operation(summary = "Получить пользователя по email")
    @GetMapping("/{email}")
    public User get(
            @Parameter(description = "Email пользователя")
            @PathVariable String email) {
        return userService.getUserByEmail(email);
    }

    @Operation(summary = "Получить список всех пользователей")
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @Operation(summary = "Пример тестового эндпоинта")
    @GetMapping("/hello")
    public String getHello(
            @Parameter(description = "Имя пользователя")
            @RequestParam(name = "name", required = false, defaultValue = "Noname") String name,
            @Parameter(description = "Фамилия пользователя")
            @RequestParam(name = "surname", required = false, defaultValue = "NoSurname") String surname) {
        return "Hello!, " + name + " " + surname;
    }
}


//package com.example.demo.controller;
//
//import com.example.demo.model.domain.User;
//import com.example.demo.model.domain.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/user-api")
//public class UserController {
//
//    private final UserService service;
//
//    @Autowired
//    public UserController(UserService service) {
//        this.service = service;
//    }
//
//    @GetMapping("/hello")
//    public String getHello(@RequestParam(name = "name", required = false, defaultValue = "Noname") String name,
//                           @RequestParam(name = "surname", required = false, defaultValue = "NoSurname") String surname) {
//        return "Hello, " + name + " " + surname;
//    }
//
//    @GetMapping("/user")
//    public List<User> listUsers() {
//        return service.listUsers();
//    }
//
//    @GetMapping("/user/{id}")
//    public User findUser(@PathVariable(name = "id") int index) {
//        return service.findUser(index);
//    }
//
//    @PostMapping(value = "/user")
//    public User createUser(@RequestBody User user) {
//        return service.createUser(user);
//    }
//}
