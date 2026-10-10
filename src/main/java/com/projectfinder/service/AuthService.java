package com.projectfinder.service;

import com.projectfinder.dto.AuthResponse;
import com.projectfinder.dto.LoginRequest;
import com.projectfinder.dto.RegisterRequest;
import com.projectfinder.entity.User;
import com.projectfinder.exception.BadRequestException;
import com.projectfinder.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("An account with email " + email + " already exists.");
        }

        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(email);
        user.setPassword(request.getPassword());
        user.setDepartment(request.getDepartment().trim());
        user.setYear(request.getYear().trim());
        user.setRole("STUDENT");

        User savedUser = userRepository.save(user);

        return new AuthResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getDepartment(),
                savedUser.getYear(),
                savedUser.getRole(),
                "Registration successful! Please login."
        );
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new BadRequestException("Invalid email or password");
        }

        return new AuthResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getDepartment(),
                user.getYear(),
                user.getRole(),
                "Login successful!"
        );
    }
}
