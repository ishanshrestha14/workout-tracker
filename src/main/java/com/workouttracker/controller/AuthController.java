package com.workouttracker.controller;

import com.workouttracker.dto.request.LoginRequest;
import com.workouttracker.dto.request.RegisterRequest;
import com.workouttracker.dto.response.JwtAuthenticationResponse;
import com.workouttracker.dto.response.UserResponse;
import com.workouttracker.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @Operation(summary = "Register a new user", description = "Create a new user account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or user already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            logger.info("Registration attempt for username: {}", registerRequest.getUsername());
            
            UserResponse userResponse = authService.register(registerRequest);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("user", userResponse);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (RuntimeException e) {
            logger.error("Registration failed: {}", e.getMessage());
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Registration failed");
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            logger.error("Unexpected error during registration: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            logger.info("Login attempt for: {}", loginRequest.getUsernameOrEmail());
            
            JwtAuthenticationResponse jwtResponse = authService.login(loginRequest);
            
            return ResponseEntity.ok(jwtResponse);
            
        } catch (RuntimeException e) {
            logger.error("Login failed: {}", e.getMessage());
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Authentication failed");
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        } catch (Exception e) {
            logger.error("Unexpected error during login: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Get current user profile", description = "Get the profile of the currently authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User profile retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        try {
            UserResponse userResponse = authService.getCurrentUser();
            return ResponseEntity.ok(userResponse);
            
        } catch (RuntimeException e) {
            logger.error("Error getting current user: {}", e.getMessage());
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "User not found");
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        } catch (Exception e) {
            logger.error("Unexpected error getting current user: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Update user profile", description = "Update the profile of the currently authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody RegisterRequest updateRequest) {
        try {
            UserResponse userResponse = authService.updateProfile(updateRequest);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Profile updated successfully");
            response.put("user", userResponse);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            logger.error("Profile update failed: {}", e.getMessage());
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Profile update failed");
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            logger.error("Unexpected error during profile update: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Change password", description = "Change the password of the currently authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid current password or new password"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> passwordChangeRequest) {
        try {
            String currentPassword = passwordChangeRequest.get("currentPassword");
            String newPassword = passwordChangeRequest.get("newPassword");
            
            if (currentPassword == null || currentPassword.trim().isEmpty()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Validation failed");
                errorResponse.put("message", "Current password is required");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            if (newPassword == null || newPassword.length() < 6) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Validation failed");
                errorResponse.put("message", "New password must be at least 6 characters long");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            authService.changePassword(currentPassword, newPassword);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Password changed successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            logger.error("Password change failed: {}", e.getMessage());
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Password change failed");
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            logger.error("Unexpected error during password change: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Check username availability", description = "Check if a username is available for registration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Username availability checked"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/check-username")
    public ResponseEntity<?> checkUsernameAvailability(@RequestParam String username) {
        try {
            boolean available = authService.isUsernameAvailable(username);
            
            Map<String, Object> response = new HashMap<>();
            response.put("username", username);
            response.put("available", available);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error checking username availability: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "Check email availability", description = "Check if an email is available for registration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email availability checked"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmailAvailability(@RequestParam String email) {
        try {
            boolean available = authService.isEmailAvailable(email);
            
            Map<String, Object> response = new HashMap<>();
            response.put("email", email);
            response.put("available", available);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error checking email availability: ", e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", "An unexpected error occurred");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Operation(summary = "User logout", description = "Logout the currently authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout successful")
    })
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        authService.logout();
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logout successful");
        
        return ResponseEntity.ok(response);
    }
}
