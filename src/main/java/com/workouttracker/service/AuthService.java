package com.workouttracker.service;

import com.workouttracker.dto.mapper.UserMapper;
import com.workouttracker.dto.request.LoginRequest;
import com.workouttracker.dto.request.RegisterRequest;
import com.workouttracker.dto.response.JwtAuthenticationResponse;
import com.workouttracker.dto.response.UserResponse;
import com.workouttracker.model.User;
import com.workouttracker.repository.UserRepository;
import com.workouttracker.security.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserMapper userMapper;

    /**
     * Register a new user
     */
    public UserResponse register(RegisterRequest registerRequest) {
        logger.info("Attempting to register user: {}", registerRequest.getUsername());

        // Check if username already exists
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Error: Username is already taken!");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        // Create new user
        User user = userMapper.toUser(registerRequest);
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        try {
            User savedUser = userRepository.save(user);
            logger.info("User registered successfully with ID: {}", savedUser.getId());
            return userMapper.toUserResponse(savedUser);
        } catch (Exception e) {
            logger.error("Error registering user: ", e);
            throw new RuntimeException("Failed to register user: " + e.getMessage());
        }
    }

    /**
     * Authenticate user and generate JWT token
     */
    public JwtAuthenticationResponse login(LoginRequest loginRequest) {
        logger.info("Attempting to authenticate user: {}", loginRequest.getUsernameOrEmail());

        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsernameOrEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate JWT token
            String jwt = jwtUtils.generateJwtToken(authentication);
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            UserResponse userResponse = userMapper.toUserResponse(user);

            logger.info("User authenticated successfully: {}", user.getUsername());

            return new JwtAuthenticationResponse(
                    jwt,
                    jwtUtils.getJwtExpirationMs(),
                    userResponse
            );

        } catch (Exception e) {
            logger.error("Authentication failed for user: {}", loginRequest.getUsernameOrEmail(), e);
            throw new RuntimeException("Invalid username/email or password");
        }
    }

    /**
     * Get current authenticated user
     */
    public UserResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("No authenticated user found");
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return userMapper.toUserResponse(user);
    }

    /**
     * Update user profile
     */
    public UserResponse updateProfile(RegisterRequest updateRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("No authenticated user found");
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update user fields (excluding username, email, and password for now)
        userMapper.updateUserFromRequest(user, updateRequest);

        try {
            User updatedUser = userRepository.save(user);
            logger.info("User profile updated successfully: {}", updatedUser.getUsername());
            return userMapper.toUserResponse(updatedUser);
        } catch (Exception e) {
            logger.error("Error updating user profile: ", e);
            throw new RuntimeException("Failed to update profile: " + e.getMessage());
        }
    }

    /**
     * Change user password
     */
    public void changePassword(String currentPassword, String newPassword) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("No authenticated user found");
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        
        try {
            userRepository.save(user);
            logger.info("Password changed successfully for user: {}", username);
        } catch (Exception e) {
            logger.error("Error changing password for user: {}", username, e);
            throw new RuntimeException("Failed to change password: " + e.getMessage());
        }
    }

    /**
     * Check if username is available
     */
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }

    /**
     * Check if email is available
     */
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }

    /**
     * Logout user (invalidate token on client side)
     */
    public void logout() {
        SecurityContextHolder.clearContext();
        logger.info("User logged out successfully");
    }
}
