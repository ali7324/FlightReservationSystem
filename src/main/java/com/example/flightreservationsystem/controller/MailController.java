package com.example.flightreservationsystem.controller;

import com.example.flightreservationsystem.dto.ApiResponse;
import com.example.flightreservationsystem.dto.request.MailTestRequest;
import com.example.flightreservationsystem.service.MailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/mail")
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;

    @PostMapping("/test-send-reservation")
    public ApiResponse<Void> sendTestReservationMail(@Valid @RequestBody MailTestRequest request) {
        mailService.sendReservationConfirmationMail(request.getPassenger(), request.getFlight());
        return ApiResponse.ok("Test reservation mail sent successfully", null);
    }
}
