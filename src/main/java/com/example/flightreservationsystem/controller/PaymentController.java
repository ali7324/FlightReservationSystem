package com.example.flightreservationsystem.controller;

import com.example.flightreservationsystem.dto.ApiResponse;
import com.example.flightreservationsystem.dto.PaymentDto;
import com.example.flightreservationsystem.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponse<String>> makePayment(@Valid @RequestBody PaymentDto dto) {
        String result = paymentService.processPayment(dto);
        return ResponseEntity.ok(ApiResponse.success(result, "Payment processed"));
    }
}
