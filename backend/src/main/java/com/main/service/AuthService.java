package com.main.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.main.dto.LoginRequest;
import com.main.dto.LoginResponse;
import com.main.dto.RegisterRequest;
import com.main.entity.User;
import com.main.exception.ResourceNotFoundException;
import com.main.repository.UserRepository;
import com.main.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public String register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                    "Email already registered"
            );
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );
        userRepository.save(user);

        return "Registration successful";
    }

    public LoginResponse login(LoginRequest request) {
        User user =userRepository
                        .findByEmail(request.getEmail())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Invalid email or password")
                        );

        boolean passwordMatches =
                passwordEncoder.matches(
                        request.getPassword(),
                        user.getPassword()
                );

        if (!passwordMatches) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String token =jwtService.generateToken(user);

        return new LoginResponse(
                token,
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}