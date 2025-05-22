package com.example.flightreservationsystem.controller;

import com.example.flightreservationsystem.dto.request.ReservationRequestDto;
import com.example.flightreservationsystem.dto.response.ReservationResponseDto;
import com.example.flightreservationsystem.entity.ReservationEntity;
import com.example.flightreservationsystem.repository.ReservationRepository;
import com.example.flightreservationsystem.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping
    public List<ReservationResponseDto> getAllReservations() {
        log.info("START: GET /reservasiya cagirildi: butun reservasiyalar sorgulanir");
        List<ReservationResponseDto> reservations = reservationService.getAllReservations();
        log.info("END: GET /rezervasiya tamamlandı: {} rezervasiya tapıldı", reservations.size());
        return reservations;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponseDto> getReservationById(@PathVariable Long id) {
        log.info("START: GET /rezervasiya/{} çağırıldı", id);
        ResponseEntity<ReservationResponseDto> response = reservationService.getReservationById(id)
                .map(reservation -> {
                    log.info("Rezervasiya tapıldı: id={}", id);
                    log.info("END: GET /rezervasiya/{} tamamlandı", id);
                    return ResponseEntity.ok(reservation);
                })
                .orElseGet(() -> {
                    log.warn("Rezervasiya tapılmadı: id={}", id);
                    log.info("END: GET /rezervasiya/{} tamamlandı - tapılmadı", id);
                    return ResponseEntity.notFound().build();
                });
        return response;
    }

    @PostMapping
    public ReservationResponseDto addReservation(@RequestBody ReservationRequestDto requestDto) {
        log.info("START: POST /rezervasiya cagirildi: yeni rezervasiya elave edilir: {}", requestDto);
        ReservationResponseDto created = reservationService.addReservation(requestDto);
        log.info("rezervasiya ugurla yenilendi: id={}", created.getId());
        log.info("END: POST /rezervasiya tamamlandı");
        return created;
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservationResponseDto> updateReservation(@PathVariable Long id, @RequestBody ReservationRequestDto requestDto) {
        log.info("START: PUT /rezervasiya/{} cagirildi: yenilenen melumatlar: {}", id, requestDto);
        ReservationResponseDto updated = reservationService.updateReservation(id, requestDto);
        if (updated != null) {
            log.info("rezervasiya ugurla yenilendi: id={}", id);
            log.info("END: PUT /rezervasiya/{} tamamlandı", id);
            return ResponseEntity.ok(updated);
        } else {
            log.warn("yenilenme ugursuz oldu: rezervasiya tapılmadı: id={}", id);
            log.info("END: PUT /rezervasiya/{} tamamlandı - tapılmadı", id);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public void deleteReservation(@PathVariable Long id) {
        log.info("START: DELETE /rezervasiya/{} cagirildi: Rezervasiya silinir", id);
        reservationService.deleteReservation(id);
        log.info("Rezervasiya ugurla silindi: id={}", id);
        log.info("END: DELETE /rezervasiya/{} tamamlandı", id);
    }
}
