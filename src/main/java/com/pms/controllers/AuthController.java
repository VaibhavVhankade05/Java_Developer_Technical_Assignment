package com.pms.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.pms.config.JwtService;
import com.pms.dto.AuthResponse;
import com.pms.dto.RefreshTokenRequest;
import com.pms.entities.RefreshToken;
import com.pms.entities.User;
import com.pms.repositories.RefreshTokenRepository;
import com.pms.repositories.UserRepository;
import com.pms.services.RefreshTokenService;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController 
{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_USER");
        userRepository.save(user);
        log.info("User registered: {}", user.getUsername());
        return "User registered successfully";
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody User request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String accessToken = jwtService.generateToken(user.getUsername());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        log.info("User logged in: {}", user.getUsername());
        
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            String refreshTokenString = request.getRefreshToken();
            log.info("Refresh token request received");
            
            
            if (refreshTokenString == null || refreshTokenString.trim().isEmpty()) {
                log.error("Refresh token is empty");
                return ResponseEntity.status(400).body(null);
            }

            RefreshToken token = refreshTokenRepository.findByToken(refreshTokenString)
                    .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

            log.debug("Found refresh token for user: {}", token.getUser().getUsername());

            refreshTokenService.verifyExpiration(token);

            User user = token.getUser();

            

            RefreshToken newRefreshToken =
                    refreshTokenService.createRefreshToken(user);

            String newAccessToken =
                    jwtService.generateToken(user.getUsername());

            log.info("Token refreshed for user: {}", user.getUsername());

            return ResponseEntity.ok(AuthResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken.getToken())
                    .build());
                    
        } catch (RuntimeException e) {
            log.error("Refresh token failed: {}", e.getMessage());
            return ResponseEntity.status(401).body(null);
        }
    }
}