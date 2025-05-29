package com.example.flightreservationsystem.controller;

import com.example.flightreservationsystem.dto.ApiResponse;
import com.example.flightreservationsystem.dto.PaymentDto;
import com.example.flightreservationsystem.service.PaymentService;
import com.example.flightreservationsystem.validation.PaymentValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentValidator paymentValidator;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> makePayment(@RequestBody PaymentDto dto, Errors errors) {
        paymentValidator.validate(dto, errors);

        if (errors.hasErrors()) {
            String errorMsg = errors.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage())
                    .collect(Collectors.joining("; "));
            return ResponseEntity.badRequest().body(ApiResponse.error(errorMsg));
        }

        String message = paymentService.processPayment(dto);
        return ResponseEntity.ok(ApiResponse.success(message));
    }
}

