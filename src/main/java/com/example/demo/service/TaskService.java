package com.example.demo.service;

import com.example.demo.entity.Task;
import com.example.demo.entity.TaskGroup;
import com.example.demo.entity.User;
import com.example.demo.entity.TaskStatus;
import com.example.demo.repository.TaskGroupRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskGroupRepository taskGroupRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository,
                       TaskGroupRepository taskGroupRepository,
                       UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.taskGroupRepository = taskGroupRepository;
        this.userRepository = userRepository;
    }

    private void checkOwner(Task task, Long userId) {
        if (!task.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Нет доступа к задаче");
        }
    }

    public List<Task> getUserTasks(Long userId) {
        return taskRepository.findByUser_Id(userId);
    }

    public Task createTask(Task task, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        task.setUser(user);

        if (task.getStatus() == null) {
            task.setStatus(TaskStatus.PLANNED);
        }

        return taskRepository.save(task);
    }

    public Task updateTask(Long taskId, Task newData, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Задача не найдена"));

        checkOwner(task, userId);

        task.setTitle(newData.getTitle());
        task.setDescription(newData.getDescription());
        task.setStatus(newData.getStatus());
        task.setGroup(newData.getGroup());

        return taskRepository.save(task);
    }

    public void deleteTask(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Задача не найдена"));

        checkOwner(task, userId);
        taskRepository.delete(task);
    }

    public Task updateStatus(Long taskId, TaskStatus status, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Задача не найдена"));

        checkOwner(task, userId);

        task.setStatus(status);
        return taskRepository.save(task);
    }

    public Task changeTaskGroup(Long taskId, Long groupId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Задача не найдена"));

        checkOwner(task, userId);

        TaskGroup group = taskGroupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Группа задач не найдена"));

        if (!group.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Нет доступа к группе");
        }

        task.setGroup(group);
        return taskRepository.save(task);
    }
}
