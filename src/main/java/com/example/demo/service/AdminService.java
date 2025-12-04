package com.example.demo.service;

import com.example.demo.entity.Task;
import com.example.demo.entity.TaskGroup;
import com.example.demo.entity.User;
import com.example.demo.entity.TaskStatus;
import com.example.demo.repository.TaskGroupRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final TaskGroupRepository taskGroupRepository;

    public AdminService(UserRepository userRepository,
                        TaskRepository taskRepository,
                        TaskGroupRepository taskGroupRepository) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.taskGroupRepository = taskGroupRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public List<TaskGroup> getAllGroups() {
        return taskGroupRepository.findAll();
    }

    public List<Task> getTasksByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
        return taskRepository.findByUser_Id(user.getId());
    }

    public Map<TaskStatus, Long> getTaskCountByStatus() {
        return taskRepository.findAll().stream()
                .collect(Collectors.groupingBy(Task::getStatus, Collectors.counting()));
    }

    public Map<String, Long> getTaskCountByGroup() {
        return taskRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        task -> task.getGroup() != null ? task.getGroup().getName() : "Без группы",
                        Collectors.counting()
                ));
    }
}
