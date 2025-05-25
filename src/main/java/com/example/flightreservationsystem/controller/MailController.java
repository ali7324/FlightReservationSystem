package com.example.flightreservationsystem.controller;

import com.example.flightreservationsystem.dto.MailDto;
import com.example.flightreservationsystem.payload.ApiResponse;
import com.example.flightreservationsystem.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/mail")
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> sendSimpleMail(@RequestBody MailDto mailDto) {
        mailService.sendSimpleMail(mailDto);
        return ResponseEntity.ok(ApiResponse.success(null, "Mail sent successfully"));
    }
}
