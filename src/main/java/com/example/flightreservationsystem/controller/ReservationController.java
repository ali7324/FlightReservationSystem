package com.example.flightreservationsystem.controller;

import com.example.flightreservationsystem.dto.ReservationDto;
import com.example.flightreservationsystem.dto.ApiResponse;
import com.example.flightreservationsystem.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    public ResponseEntity<ApiResponse<ReservationDto>> createReservation(@RequestBody ReservationDto reservationDto) {
        ReservationDto created = reservationService.createReservation(reservationDto);
        return ResponseEntity.ok(ApiResponse.success(created, "Reservation created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ReservationDto>> updateReservation(
            @PathVariable Long id,
            @RequestBody ReservationDto reservationDto
    ) {
        ReservationDto updated = reservationService.updateReservationOrThrow(id, reservationDto);
        return ResponseEntity.ok(ApiResponse.success(updated, "Reservation updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Reservation deleted successfully"));
    }

}
