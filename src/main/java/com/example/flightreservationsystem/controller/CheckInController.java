// REST qatında 2 endpoint:
// 1) /eligibility  -> yalnız yoxlama (front "Check" düyməsi üçün)
// 2) /{reservationId} -> real check-in (front "Check-in" düyməsi üçün)

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

    // USER və ADMIN hər ikisi baxa bilər (SecurityConfig-də qorunur)
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/eligibility")
    public ApiResponse<CheckInEligibilityDto> eligibility(@RequestParam Long reservationId) {
        //GET /api/v1/check-in/eligibility?reservationId=10
        return ApiResponse.ok("Eligibility calculated", checkInService.checkEligibility(reservationId));
    }

    // USER və ADMIN real check-in edə bilər
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping("/{reservationId}")
    public ApiResponse<BoardingPassDto> checkIn(@PathVariable Long reservationId) {
        //  POST /api/v1/check-in/15
        return ApiResponse.ok("Checked in successfully", checkInService.checkIn(reservationId));
    }
}
