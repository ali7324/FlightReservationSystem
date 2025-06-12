package com.example.flightreservationsystem.controller;

import com.example.flightreservationsystem.dto.ReservationDto;
import com.example.flightreservationsystem.dto.ApiResponse;
import com.example.flightreservationsystem.enums.ReservationStatus;
import com.example.flightreservationsystem.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReservationDto>>> getAllReservations() {
        return ResponseEntity.ok(ApiResponse.success(
                reservationService.getAllReservations(),
                "All reservations retrieved successfully"
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReservationDto>> getReservationById(@PathVariable Long id) {
        ReservationDto reservation = reservationService.getReservationOrThrow(id);
        return ResponseEntity.ok(ApiResponse.success(reservation, "Reservation retrieved successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReservationDto>> createReservation(@Valid @RequestBody ReservationDto reservationDto) {
        ReservationDto created = reservationService.createReservation(reservationDto);
        return ResponseEntity.ok(ApiResponse.success(created, "Reservation created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ReservationDto>> updateReservation(
            @PathVariable Long id,
            @Valid @RequestBody ReservationDto reservationDto
    ) {
        ReservationDto updated = reservationService.updateReservationOrThrow(id, reservationDto);
        return ResponseEntity.ok(ApiResponse.success(updated, "Reservation updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Reservation deleted successfully"));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ReservationDto>> updateReservationStatus(
            @PathVariable Long id,
            @RequestParam ReservationStatus status) {

        ReservationDto updated = reservationService.updateReservationStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(updated, "Reservation status updated successfully"));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<ReservationDto>> cancelReservation(@PathVariable Long id) {
        ReservationDto cancelled = reservationService.cancelReservation(id);
        return ResponseEntity.ok(ApiResponse.success(cancelled, "Reservation cancelled successfully"));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<ReservationDto>>> getCancelledReservations() {
        return ResponseEntity.ok(ApiResponse.success(
                reservationService.getReservationHistory(),
                "Cancelled reservation history"
        ));
    }
}
