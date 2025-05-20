package com.example.flightreservationsystem.controller;

import com.example.flightreservationsystem.entity.ReservationEntity;
import com.example.flightreservationsystem.repository.ReservationRepository;
import com.example.flightreservationsystem.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping
    public List<ReservationEntity> getAllReservations() {
        return reservationService.getAllReservations();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationEntity> getReservationById(@PathVariable Long id) {
        return reservationService.getReservationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ReservationEntity addReservation(@RequestBody ReservationEntity reservation) {
        return reservationService.addReservation(reservation);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservationEntity> updateReservation(@PathVariable Long id, @RequestBody ReservationEntity reservation) {
        ReservationEntity updated = reservationService.updateReservation(id, reservation);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public void deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
    }
}
