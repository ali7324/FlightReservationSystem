package com.example.flightreservationsystem.controller;

import com.example.flightreservationsystem.dto.ApiResponse;
import com.example.flightreservationsystem.dto.request.MailTestRequest;
import com.example.flightreservationsystem.service.MailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/mail")
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;

    @PostMapping("/test-send-reservation")
    public ApiResponse<Void> sendTestReservationMail(@Valid @RequestBody MailTestRequest request) {
        try {
            mailService.sendReservationConfirmationMail(request.getPassenger(), request.getFlight());
            return ApiResponse.success("Test reservation mail sent successfully", null);
        } catch (Exception e) {
            return ApiResponse.error("Error sending test reservation mail: " + e.getMessage());
        }
    }
}