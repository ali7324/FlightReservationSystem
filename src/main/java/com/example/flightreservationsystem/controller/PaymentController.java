package com.example.flightreservationsystem.controller;

import com.example.flightreservationsystem.dto.ApiResponse;
import com.example.flightreservationsystem.dto.PaymentDto;
import com.example.flightreservationsystem.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ApiResponse<String> makePayment(@Valid @RequestBody PaymentDto dto) {
        try {
            String message = paymentService.processPayment(dto);
            return ApiResponse.success("Payment processed successfully", message);
        } catch (Exception e) {
            return ApiResponse.error("Payment failed: " + e.getMessage());
        }
    }
}