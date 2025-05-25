package com.example.flightreservationsystem.controller;

import com.example.flightreservationsystem.dto.ReservationDto;
import com.example.flightreservationsystem.payload.ApiResponse;
import com.example.flightreservationsystem.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReservationDto>>> getAllReservations() {
        List<ReservationDto> reservations = reservationService.getAllReservations();
        return ResponseEntity.ok(ApiResponse.success(reservations, "All reservations retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReservationDto>> getReservationById(@PathVariable Long id) {
        Optional<ReservationDto> reservation = reservationService.getReservationById(id);
        return reservation
                .map(r -> ResponseEntity.ok(ApiResponse.success(r, "Reservation retrieved successfully")))
                .orElseGet(() -> ResponseEntity.status(404).body(ApiResponse.error("Reservation not found")));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createReservation(@RequestBody ReservationDto reservationDto) {
        reservationService.createReservation(reservationDto);
        return ResponseEntity.ok(ApiResponse.success(null, "Reservation created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ReservationDto>> updateReservation(
            @PathVariable Long id,
            @RequestBody ReservationDto reservationDto
    ) {
        Optional<ReservationDto> updated = reservationService.updateReservation(id, reservationDto);
        return updated
                .map(r -> ResponseEntity.ok(ApiResponse.success(r, "Reservation updated successfully")))
                .orElseGet(() -> ResponseEntity.status(404).body(ApiResponse.error("Reservation not found")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Reservation deleted successfully"));
    }

}
