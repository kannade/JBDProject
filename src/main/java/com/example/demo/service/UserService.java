package com.example.demo.service;

import com.example.demo.dto.AuthResponse;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import com.example.demo.entity.Role;

@Service
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       JwtService jwtService,
                       @Lazy PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
    }

    public User createUser(String name, String email, Role role) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email уже занят: " + email);
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setRole(role != null ? role : Role.ADMIN);
        return userRepository.save(user);
    }

    public User register(String name, String email, String password, Role role) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email уже занят");
        }
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role != null ? role : Role.USER);
        return userRepository.save(user);
    }

    public AuthResponse login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Неверный пароль");
        }
        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> searchUsersByName(String name) {
        return userRepository.findByNameContains(name);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }
}
