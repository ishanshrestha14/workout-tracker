package com.workouttracker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthenticationResponse {
    
    private String accessToken;
    private String tokenType = "Bearer";
    private long expiresIn;
    private LocalDateTime expiresAt;
    private UserResponse user;

    public JwtAuthenticationResponse(String accessToken, long expiresIn, UserResponse user) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.expiresAt = LocalDateTime.now().plusSeconds(expiresIn / 1000);
        this.user = user;
        this.tokenType = "Bearer";
    }
}
