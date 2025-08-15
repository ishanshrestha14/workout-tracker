package com.workouttracker.dto.mapper;

import com.workouttracker.dto.request.RegisterRequest;
import com.workouttracker.dto.response.UserResponse;
import com.workouttracker.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    /**
     * Convert User entity to UserResponse DTO
     */
    public UserResponse toUserResponse(User user) {
        if (user == null) {
            return null;
        }

        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setDateOfBirth(user.getDateOfBirth());
        response.setGender(user.getGender());
        response.setHeightCm(user.getHeightCm());
        response.setWeightKg(user.getWeightKg());
        response.setActivityLevel(user.getActivityLevel());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        response.setEnabled(user.isEnabled());

        return response;
    }

    /**
     * Convert RegisterRequest DTO to User entity
     */
    public User toUser(RegisterRequest registerRequest) {
        if (registerRequest == null) {
            return null;
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(registerRequest.getPassword()); // Will be encoded by service
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setDateOfBirth(registerRequest.getDateOfBirth());
        user.setGender(registerRequest.getGender());
        user.setHeightCm(registerRequest.getHeightCm());
        user.setWeightKg(registerRequest.getWeightKg());
        user.setActivityLevel(registerRequest.getActivityLevel());

        return user;
    }

    /**
     * Update existing User entity with data from RegisterRequest
     */
    public void updateUserFromRequest(User user, RegisterRequest request) {
        if (user == null || request == null) {
            return;
        }

        // Don't update username and email here - they require special handling
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setGender(request.getGender());
        user.setHeightCm(request.getHeightCm());
        user.setWeightKg(request.getWeightKg());
        user.setActivityLevel(request.getActivityLevel());
    }

    /**
     * Create a minimal UserResponse with basic info
     */
    public UserResponse toBasicUserResponse(User user) {
        if (user == null) {
            return null;
        }

        return new UserResponse(user.getId(), user.getUsername(), user.getEmail());
    }
}
