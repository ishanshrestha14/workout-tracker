package com.workouttracker.service;

import com.workouttracker.model.User;
import com.workouttracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminBootstrapServiceTest {

    private static final String STRONG_PASSWORD = "correct-horse-battery";

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminBootstrapService adminBootstrapService;

    @Test
    void doesNothingWhenNoAdminIsConfigured() {
        configure("", "", "");

        adminBootstrapService.run();

        verifyNoInteractions(userRepository);
    }

    @Test
    void createsAdminWhenUsernameIsFree() {
        configure("admin", "admin@example.com", STRONG_PASSWORD);
        when(userRepository.findByUsername("admin")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(STRONG_PASSWORD)).thenReturn("hashed");

        adminBootstrapService.run();

        ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(saved.capture());
        assertThat(saved.getValue().getRole()).isEqualTo(User.Role.ADMIN);
        assertThat(saved.getValue().getPassword()).isEqualTo("hashed");
    }

    @Test
    void neverPromotesAnExistingRegularUser() {
        configure("admin", "admin@example.com", STRONG_PASSWORD);
        User squatter = new User("admin", "someone@example.com", "hash");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(squatter));

        adminBootstrapService.run();

        assertThat(squatter.getRole()).isEqualTo(User.Role.USER);
        verify(userRepository, never()).save(any());
    }

    @Test
    void refusesShortPassword() {
        configure("admin", "admin@example.com", "short");

        assertThatThrownBy(() -> adminBootstrapService.run())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("at least 12 characters");
        verifyNoInteractions(userRepository);
    }

    private void configure(String username, String email, String password) {
        ReflectionTestUtils.setField(adminBootstrapService, "username", username);
        ReflectionTestUtils.setField(adminBootstrapService, "email", email);
        ReflectionTestUtils.setField(adminBootstrapService, "password", password);
    }
}
