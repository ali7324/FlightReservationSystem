package com.example.flightreservationsystem.controller;

import com.example.flightreservationsystem.dto.AuthDto;
import com.example.flightreservationsystem.dto.ApiResponse;
import com.example.flightreservationsystem.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authService;

    @PostMapping("/register")
    public ApiResponse<AuthDto> register(@Valid @RequestBody AuthDto request) {
        try {
            AuthDto response = authService.register(request);
            return ApiResponse.success("User registered successfully", response);
        } catch (Exception e) {
            return ApiResponse.error("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ApiResponse<AuthDto> login(@Valid @RequestBody AuthDto request) {
        try {
            AuthDto response = authService.login(request);
            return ApiResponse.success("Login successful", response);
        } catch (Exception e) {
            return ApiResponse.error("Login failed: " + e.getMessage());
        }
    }
}