// com.example.flightreservationsystem.controller.CheckInController
package com.example.flightreservationsystem.controller;

import com.example.flightreservationsystem.dto.ApiResponse;
import com.example.flightreservationsystem.dto.BoardingPassDto;
import com.example.flightreservationsystem.dto.CheckInEligibilityDto;
import com.example.flightreservationsystem.service.CheckInService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/check-in")
@RequiredArgsConstructor
public class CheckInController {

    private final CheckInService checkInService;

    // user və admin gore bilsin
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/eligibility")
    public ApiResponse<CheckInEligibilityDto> eligibility(@RequestParam Long reservationId) {
        return ApiResponse.ok("Eligibility calculated", checkInService.checkEligibility(reservationId));
    }

    // user və admin nese elesin
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping("/{reservationId}")
    public ApiResponse<BoardingPassDto> checkIn(@PathVariable Long reservationId) {
        return ApiResponse.ok("Checked in successfully", checkInService.checkIn(reservationId));
    }
}
