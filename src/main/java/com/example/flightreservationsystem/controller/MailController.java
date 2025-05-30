package com.example.flightreservationsystem.controller;

import com.example.flightreservationsystem.dto.ApiResponse;
import com.example.flightreservationsystem.dto.request.MailTestRequest;
import com.example.flightreservationsystem.service.MailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/mail")
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;

    @PostMapping("/test-send-reservation")
    public ResponseEntity<ApiResponse<Void>> sendTestReservationMail(@Valid @RequestBody MailTestRequest request) {
        mailService.sendReservationConfirmationMail(request.getPassenger(), request.getFlight());
        return ResponseEntity.ok(ApiResponse.success(null, "Test reservation mail sent successfully"));
    }
}
