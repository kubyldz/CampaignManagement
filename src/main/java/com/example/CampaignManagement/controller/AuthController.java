package com.example.CampaignManagement.controller;

import com.example.CampaignManagement.model.LoginRequest;
import com.example.CampaignManagement.model.User;
import com.example.CampaignManagement.repository.UserRepository;
import com.example.CampaignManagement.security.JwtUtil;
import com.example.CampaignManagement.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthService authService;

    public AuthController(
            UserRepository userRepository,
            JwtUtil jwtUtil,
            BCryptPasswordEncoder passwordEncoder,
            AuthService authService) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User user) {
        try {
            logger.info("Attempting to register user: {}", user.getUsername());

            if (userRepository.findByUsername(user.getUsername()).isPresent()) {
                logger.warn("Registration failed: Username {} already exists", user.getUsername());
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Username is already taken"));
            }

            user.setRole(user.getRole() == null ? "USER" : user.getRole());
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            User savedUser = userRepository.save(user);
            logger.info("User registered successfully: {}", savedUser.getUsername());

            String token = jwtUtil.generateToken(savedUser);

            return ResponseEntity.ok()
                    .body(Map.of(
                            "message", "User registered successfully",
                            "token", token
                    ));

        } catch (Exception e) {
            logger.error("Registration failed", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Registration failed: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            logger.info("Login request: " + loginRequest.getUsername());
            String token = authService.authenticateUser(loginRequest.getUsername(), loginRequest.getPassword());
            return ResponseEntity.ok().body("{\"token\": \"" + token + "\"}");
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
