package com.example.flightreservationsystem.controller;

import com.example.flightreservationsystem.dto.AuthDto;
import com.example.flightreservationsystem.dto.ApiResponse;
import com.example.flightreservationsystem.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authService;

    @PostMapping("/register")
    public ApiResponse<AuthDto> register(@Valid @RequestBody AuthDto request) {

        AuthDto response = authService.register(request);
        return ApiResponse.ok("User registered successfully", response);
    }

    @PostMapping("/login")
    public ApiResponse<AuthDto> login(@Valid @RequestBody AuthDto request) {
        AuthDto response = authService.login(request);
        return ApiResponse.ok("Login successful", response);
    }
}
