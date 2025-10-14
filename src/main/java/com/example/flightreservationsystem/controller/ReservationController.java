package com.example.flightreservationsystem.controller;

import com.example.flightreservationsystem.dto.ApiResponse;
import com.example.flightreservationsystem.dto.ReservationDto;
import com.example.flightreservationsystem.enums.ReservationStatus;
import com.example.flightreservationsystem.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping
    public ApiResponse<List<ReservationDto>> getAllReservations() {
        return ApiResponse.ok("All reservations retrieved successfully", reservationService.getAllReservations());
    }

    @GetMapping("/{id}")
    public ApiResponse<ReservationDto> getReservationById(@PathVariable Long id) {
        return ApiResponse.ok("Reservation retrieved successfully", reservationService.getReservationOrThrow(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping
    public ApiResponse<ReservationDto> createReservation(@Valid @RequestBody ReservationDto reservationDto) {
        return ApiResponse.ok("Reservation created successfully", reservationService.createReservation(reservationDto));
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping("/{id}")
    public ApiResponse<ReservationDto> updateReservation(@PathVariable Long id,
                                                         @Valid @RequestBody ReservationDto reservationDto) {
        return ApiResponse.ok("Reservation updated successfully", reservationService.updateReservationOrThrow(id, reservationDto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ApiResponse.ok("Reservation deleted successfully", null);
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PatchMapping("/{id}/status")
    public ApiResponse<ReservationDto> updateReservationStatus(@PathVariable Long id,
                                                               @RequestParam ReservationStatus status) {
        return ApiResponse.ok("Reservation status updated successfully", reservationService.updateReservationStatus(id, status));
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PatchMapping("/{id}/cancel")
    public ApiResponse<ReservationDto> cancelReservation(@PathVariable Long id) {
        return ApiResponse.ok("Reservation cancelled successfully", reservationService.cancelReservation(id));
    }

    @GetMapping("/history")
    public ApiResponse<List<ReservationDto>> getCancelledReservations() {
        return ApiResponse.ok("Cancelled reservation history retrieved successfully", reservationService.getReservationHistory());
    }
}
