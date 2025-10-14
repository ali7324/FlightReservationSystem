package com.example.flightreservationsystem.controller;

import com.example.flightreservationsystem.dto.ApiResponse;
import com.example.flightreservationsystem.dto.PaymentDto;
import com.example.flightreservationsystem.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping
    public ApiResponse<String> makePayment(@Valid @RequestBody PaymentDto dto) {
        return ApiResponse.ok("Payment processed successfully", paymentService.processPayment(dto));
    }
}
