package com.example.CampaignManagement.service;

import com.example.CampaignManagement.repository.UserRepository;
import com.example.CampaignManagement.security.JwtUtil;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AuthService {
    private final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil, UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    public String authenticateUser(String username, String password) {
        return userRepository.findByUsername(username)
                .map(user -> {
                    logger.info("Password in the DB: " + user.getPassword());
                    logger.info("Password that user logged: " + password);

                    if (!passwordEncoder.matches(password, user.getPassword())) {
                        logger.error("Passwords doesn't match.");
                        throw new RuntimeException("Invalid credentials");
                    }

                    logger.info("User logged in successfully.");
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    String token = jwtUtil.generateToken(userDetails);

                    logger.info("token: {}", token);
                    logger.info("Token time: {}", new java.util.Date());

                    return token;
                })
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
