package com.example.flightreservationsystem.controller;

import com.example.flightreservationsystem.dto.ReservationDto;
import com.example.flightreservationsystem.dto.ApiResponse;
import com.example.flightreservationsystem.enums.ReservationStatus;
import com.example.flightreservationsystem.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping
    public ApiResponse<List<ReservationDto>> getAllReservations() {
        try {
            List<ReservationDto> reservations = reservationService.getAllReservations();
            return ApiResponse.success("All reservations retrieved successfully", reservations);
        } catch (Exception e) {
            return ApiResponse.error("Error retrieving reservations: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<ReservationDto> getReservationById(@PathVariable Long id) {
        try {
            ReservationDto reservation = reservationService.getReservationOrThrow(id);
            return ApiResponse.success("Reservation retrieved successfully", reservation);
        } catch (Exception e) {
            return ApiResponse.error("Error retrieving reservation: " + e.getMessage());
        }
    }

    @PostMapping
    public ApiResponse<ReservationDto> createReservation(@Valid @RequestBody ReservationDto reservationDto) {
        try {
            ReservationDto created = reservationService.createReservation(reservationDto);
            return ApiResponse.success("Reservation created successfully", created);
        } catch (Exception e) {
            return ApiResponse.error("Error creating reservation: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResponse<ReservationDto> updateReservation(
            @PathVariable Long id,
            @Valid @RequestBody ReservationDto reservationDto
    ) {
        try {
            ReservationDto updated = reservationService.updateReservationOrThrow(id, reservationDto);
            return ApiResponse.success("Reservation updated successfully", updated);
        } catch (Exception e) {
            return ApiResponse.error("Error updating reservation: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteReservation(@PathVariable Long id) {
        try {
            reservationService.deleteReservation(id);
            return ApiResponse.success("Reservation deleted successfully", null);
        } catch (Exception e) {
            return ApiResponse.error("Error deleting reservation: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<ReservationDto> updateReservationStatus(
            @PathVariable Long id,
            @RequestParam ReservationStatus status) {
        try {
            ReservationDto updated = reservationService.updateReservationStatus(id, status);
            return ApiResponse.success("Reservation status updated successfully", updated);
        } catch (Exception e) {
            return ApiResponse.error("Error updating reservation status: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}/cancel")
    public ApiResponse<ReservationDto> cancelReservation(@PathVariable Long id) {
        try {
            ReservationDto cancelled = reservationService.cancelReservation(id);
            return ApiResponse.success("Reservation cancelled successfully", cancelled);
        } catch (Exception e) {
            return ApiResponse.error("Error cancelling reservation: " + e.getMessage());
        }
    }

    @GetMapping("/history")
    public ApiResponse<List<ReservationDto>> getCancelledReservations() {
        try {
            List<ReservationDto> history = reservationService.getReservationHistory();
            return ApiResponse.success("Cancelled reservation history retrieved successfully", history);
        } catch (Exception e) {
            return ApiResponse.error("Error retrieving reservation history: " + e.getMessage());
        }
    }
}