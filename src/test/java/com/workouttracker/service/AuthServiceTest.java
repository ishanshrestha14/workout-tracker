package com.workouttracker.service;

import com.workouttracker.dto.mapper.UserMapper;
import com.workouttracker.dto.request.LoginRequest;
import com.workouttracker.dto.request.RegisterRequest;
import com.workouttracker.dto.response.JwtAuthenticationResponse;
import com.workouttracker.dto.response.UserResponse;
import com.workouttracker.model.User;
import com.workouttracker.repository.UserRepository;
import com.workouttracker.security.JwtUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthService authService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void register_savesUserWithEncodedPassword() {
        RegisterRequest request = registerRequest("alice", "alice@example.com", "secret123");
        User mapped = new User("alice", "alice@example.com", null);
        UserResponse expected = new UserResponse();
        expected.setUsername("alice");

        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(userMapper.toUser(request)).thenReturn(mapped);
        when(passwordEncoder.encode("secret123")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userMapper.toUserResponse(mapped)).thenReturn(expected);

        UserResponse result = authService.register(request);

        ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(saved.capture());
        assertThat(saved.getValue().getPassword()).isEqualTo("hashed");
        assertThat(result).isSameAs(expected);
    }

    @Test
    void register_rejectsTakenUsername() {
        when(userRepository.existsByUsername("alice")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest("alice", "alice@example.com", "secret123")))
                .hasMessageContaining("Username is already taken");
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_rejectsEmailInUse() {
        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest("alice", "alice@example.com", "secret123")))
                .hasMessageContaining("Email is already in use");
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_returnsJwtForValidCredentials() {
        User user = new User("alice", "alice@example.com", "hashed");
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("jwt-token");
        when(jwtUtils.getJwtExpirationMs()).thenReturn(86_400_000);
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(userMapper.toUserResponse(user)).thenReturn(new UserResponse());

        JwtAuthenticationResponse response = authService.login(new LoginRequest("alice", "secret123"));

        assertThat(response.getAccessToken()).isEqualTo("jwt-token");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(response.getExpiresIn()).isEqualTo(86_400_000);
    }

    @Test
    void login_failsWithGenericMessageForBadCredentials() {
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(new LoginRequest("alice", "wrong")))
                .hasMessage("Invalid username/email or password");
        verify(jwtUtils, never()).generateJwtToken(any(Authentication.class));
    }

    @Test
    void changePassword_rejectsIncorrectCurrentPassword() {
        User user = authenticateAs("alice", "hashed");
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        assertThatThrownBy(() -> authService.changePassword("wrong", "newSecret1"))
                .hasMessage("Current password is incorrect");
        verify(userRepository, never()).save(user);
    }

    @Test
    void changePassword_storesNewEncodedPassword() {
        User user = authenticateAs("alice", "hashed");
        when(passwordEncoder.matches("secret123", "hashed")).thenReturn(true);
        when(passwordEncoder.encode("newSecret1")).thenReturn("new-hash");

        authService.changePassword("secret123", "newSecret1");

        assertThat(user.getPassword()).isEqualTo("new-hash");
        verify(userRepository).save(user);
    }

    private User authenticateAs(String username, String passwordHash) {
        User user = new User(username, username + "@example.com", passwordHash);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, null, List.of()));
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        return user;
    }

    private static RegisterRequest registerRequest(String username, String email, String password) {
        RegisterRequest request = new RegisterRequest();
        request.setUsername(username);
        request.setEmail(email);
        request.setPassword(password);
        return request;
    }
}
