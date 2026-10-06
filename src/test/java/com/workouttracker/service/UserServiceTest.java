package com.workouttracker.service;

import com.workouttracker.dto.mapper.UserMapper;
import com.workouttracker.model.User;
import com.workouttracker.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUserEntity_failsWithoutAuthentication() {
        assertThatThrownBy(() -> userService.getCurrentUserEntity())
                .hasMessage("No authenticated user found");
    }

    @Test
    void getCurrentUserEntity_loadsTheAuthenticatedUser() {
        User alice = new User("alice", "alice@example.com", "hashed");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("alice", null, List.of()));
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(alice));

        assertThat(userService.getCurrentUserEntity()).isSameAs(alice);
    }

    @Test
    void updateUserStatus_disablesUser() {
        User alice = new User("alice", "alice@example.com", "hashed");
        when(userRepository.findById(1L)).thenReturn(Optional.of(alice));
        when(userRepository.save(alice)).thenReturn(alice);

        userService.updateUserStatus(1L, false);

        assertThat(alice.isEnabled()).isFalse();
        verify(userRepository).save(alice);
    }

    @Test
    void getUserStats_derivesInactiveCount() {
        when(userRepository.count()).thenReturn(10L);
        when(userRepository.countActiveUsers()).thenReturn(7L);

        UserService.UserStats stats = userService.getUserStats();

        assertThat(stats.getInactiveUsers()).isEqualTo(3);
    }
}
