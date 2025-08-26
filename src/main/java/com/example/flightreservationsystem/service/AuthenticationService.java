package com.example.flightreservationsystem.service;

import com.example.flightreservationsystem.dto.AuthDto;
import com.example.flightreservationsystem.entity.UserEntity;
import com.example.flightreservationsystem.repository.UserRepository;
import com.example.flightreservationsystem.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthDto register(AuthDto request) {
        log.info("Register method started for email: {}", request.getEmail());

        UserEntity user = UserEntity.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        userRepository.save(user);
        String token = jwtService.generateToken(user);

        log.info("Register method completed successfully for email: {}", request.getEmail());
        return AuthDto.builder()
                .email(user.getEmail())
                .role(user.getRole())
                .token(token)
                .build();
    }

    public AuthDto login(AuthDto request) {
        log.info("Login method started for email: {}", request.getEmail());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.error("Login failed - User not found: {}", request.getEmail());
                    return new RuntimeException("User not found");
                });

        String token = jwtService.generateToken(user);

        log.info("Login method completed successfully for email: {}", request.getEmail());
        return AuthDto.builder()
                .email(user.getEmail())
                .role(user.getRole())
                .token(token)
                .build();
    }
}