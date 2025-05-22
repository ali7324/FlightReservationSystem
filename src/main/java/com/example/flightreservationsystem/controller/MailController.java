package com.example.flightreservationsystem.controller;

import com.example.flightreservationsystem.dto.MailDto;
import com.example.flightreservationsystem.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/mail")
@RequiredArgsConstructor
public class MailController {
    private final MailService mailService;

    @PostMapping("/simple")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void sendSimpleMail(@RequestBody MailDto mailDto) {
            mailService.sendSimpleMail(mailDto);
    }
}
