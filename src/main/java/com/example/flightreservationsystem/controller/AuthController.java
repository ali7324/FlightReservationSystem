package com.example.flightreservationsystem.controller;

import com.example.flightreservationsystem.dto.AuthDto;
import com.example.flightreservationsystem.dto.ApiResponse;
import com.example.flightreservationsystem.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authService;


    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthDto>> register(@RequestBody AuthDto request) {
        AuthDto response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthDto>> login(@RequestBody AuthDto request) {
        AuthDto response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
