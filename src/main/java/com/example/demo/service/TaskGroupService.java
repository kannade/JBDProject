package com.example.demo.service;

import com.example.demo.entity.TaskGroup;
import com.example.demo.entity.User;
import com.example.demo.repository.TaskGroupRepository;
import com.example.demo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskGroupService {

    private final TaskGroupRepository taskGroupRepository;
    private final UserRepository userRepository;

    public TaskGroupService(TaskGroupRepository taskGroupRepository, UserRepository userRepository) {
        this.taskGroupRepository = taskGroupRepository;
        this.userRepository = userRepository;
    }

    private void checkOwner(TaskGroup group, Long userId) {
        if (!group.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Нет доступа к группе задач");
        }
    }

    public List<TaskGroup> getUserGroups(Long userId) {
        return taskGroupRepository.findByUser_Id(userId);
    }

    public TaskGroup createGroup(String name, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        TaskGroup group = new TaskGroup();
        group.setName(name);
        group.setUser(user);

        return taskGroupRepository.save(group);
    }

    public TaskGroup updateGroup(Long groupId, String newName, Long userId) {
        TaskGroup group = taskGroupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Группа задач не найдена"));

        checkOwner(group, userId);
        group.setName(newName);

        return taskGroupRepository.save(group);
    }

    public void deleteGroup(Long groupId, Long userId) {
        TaskGroup group = taskGroupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Группа задач не найдена"));

        checkOwner(group, userId);
        taskGroupRepository.delete(group);
    }
}
