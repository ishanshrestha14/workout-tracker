package com.workouttracker.service;

import com.workouttracker.dto.mapper.UserMapper;
import com.workouttracker.dto.response.UserResponse;
import com.workouttracker.model.User;
import com.workouttracker.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    /**
     * Get user by ID
     */
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        return userMapper.toUserResponse(user);
    }

    /**
     * Get user by username
     */
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        
        return userMapper.toUserResponse(user);
    }

    /**
     * Get current authenticated user entity (for internal use)
     */
    public User getCurrentUserEntity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("No authenticated user found");
        }

        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Get all users (admin functionality - can be restricted later)
     */
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get users with pagination
     */
    public Page<UserResponse> getUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return users.map(userMapper::toUserResponse);
    }

    /**
     * Get active users count
     */
    public long getActiveUsersCount() {
        return userRepository.countActiveUsers();
    }

    /**
     * Enable or disable user account
     */
    @Transactional
    public UserResponse updateUserStatus(Long userId, boolean enabled) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        user.setEnabled(enabled);
        User updatedUser = userRepository.save(user);
        
        logger.info("User {} status updated to: {}", user.getUsername(), enabled ? "enabled" : "disabled");
        return userMapper.toUserResponse(updatedUser);
    }

    /**
     * Delete user account
     */
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        userRepository.delete(user);
        logger.info("User deleted: {}", user.getUsername());
    }

    /**
     * Check if user exists by username
     */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Check if user exists by email
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Search users by username (partial match)
     */
    public List<UserResponse> searchUsersByUsername(String username) {
        // This would require a custom query method in UserRepository
        // For now, we'll implement basic functionality
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            return List.of(userMapper.toUserResponse(user));
        }
        return List.of();
    }

    /**
     * Get user statistics (for admin dashboard)
     */
    public UserStats getUserStats() {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countActiveUsers();
        
        return new UserStats(totalUsers, activeUsers);
    }

    /**
     * Inner class for user statistics
     */
    public static class UserStats {
        private long totalUsers;
        private long activeUsers;
        private long inactiveUsers;

        public UserStats(long totalUsers, long activeUsers) {
            this.totalUsers = totalUsers;
            this.activeUsers = activeUsers;
            this.inactiveUsers = totalUsers - activeUsers;
        }

        // Getters
        public long getTotalUsers() {
            return totalUsers;
        }

        public long getActiveUsers() {
            return activeUsers;
        }

        public long getInactiveUsers() {
            return inactiveUsers;
        }
    }
}
