package com.example.flightreservationsystem.service;

import com.example.flightreservationsystem.dto.AuthDto;
import com.example.flightreservationsystem.entity.UserEntity;
import com.example.flightreservationsystem.enums.Role;
import com.example.flightreservationsystem.exception.ResourceNotFoundException;
import com.example.flightreservationsystem.repository.UserRepository;
import com.example.flightreservationsystem.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthDto register(AuthDto request) {
        log.info("Register started: {}", request.getEmail());

        userRepository.findByEmail(request.getEmail()).ifPresent(u -> {
            throw new IllegalArgumentException("Email is already in use");
        });

        Role role = request.getRole() != null ? request.getRole() : Role.USER;

        UserEntity user = UserEntity.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .build();

        userRepository.save(user);
        String token = jwtService.generateToken(user);

        log.info("Register completed: {}", request.getEmail());
        return AuthDto.builder()
                .email(user.getEmail())
                .role(user.getRole())
                .token(token)
                .build();
    }

    public AuthDto login(AuthDto request) {
        log.info("Login started: {}", request.getEmail());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getEmail()));

        String token = jwtService.generateToken(user);

        log.info("Login completed: {}", request.getEmail());
        return AuthDto.builder()
                .email(user.getEmail())
                .role(user.getRole())
                .token(token)
                .build();
    }
}
