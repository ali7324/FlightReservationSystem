package com.example.flightreservationsystem.controller;

import com.example.flightreservationsystem.dto.request.FlightRequestDto;
import com.example.flightreservationsystem.dto.response.FlightResponseDto;
import com.example.flightreservationsystem.entity.FlightEntity;
import com.example.flightreservationsystem.service.FlightService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/flight")
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;

    @GetMapping
    public List<FlightResponseDto> getAllFlights() {
        log.info("START: GET /flight");
        List<FlightResponseDto> flights = flightService.getAllFlights();
        log.info("END: GET /flight");
        return flights;
    }

    @GetMapping("/{id}")
    public ResponseEntity<FlightResponseDto> getFlightById(@PathVariable Long id) {
        log.info("START: GET /flight/{}", id);
        ResponseEntity<FlightResponseDto> response = flightService.getFlightById(id)
                .map(flight -> {
                    log.info("Flight id={} tapıldı", id);
                    log.info("END: GET /flight/{} tamamlandı", id);
                    return ResponseEntity.ok(flight);
                })
                .orElseGet(() -> {
                    log.warn("Flight id={} tapılmadı", id);
                    log.info("END: GET /flight/{} tamamlandı", id);
                    return ResponseEntity.notFound().build();
                });
        return response;
    }

    @PostMapping
    public FlightResponseDto addFlight(@RequestBody FlightRequestDto flightRequestDto) {
        log.info("START: POST /flight cagirildi: ucus elave edilir: {}", flightRequestDto);
        FlightResponseDto created = flightService.addFlight(flightRequestDto);
        log.info("ugurla elave edildi: Flight id={}", created.getId());
        log.info("END: POST /flight tamamlandı");
        return created;
    }

    @PutMapping("/{id}")
    public ResponseEntity<FlightResponseDto> updateFlight(@PathVariable Long id, @RequestBody FlightRequestDto flightRequestDto) {
        log.info("START: PUT /flight/{} cagirildi yeni elave edilen melumatlar: {}", id, flightRequestDto);
        FlightResponseDto updatedFlight = flightService.updateFlight(id, flightRequestDto);
        if (updatedFlight != null) {
            log.info("ugurla yenilendi: Flight id={}", id);
            log.info("END: PUT /flight/{} tamamlandı", id);
            return ResponseEntity.ok(updatedFlight);
        } else {
            log.warn("ugursuz oldu: Flight id={} tapılmadı", id);
            log.info("END: PUT /flight/{} tamamlandi tapilmadi", id);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public void deleteFlight(@PathVariable Long id) {
        log.info("START: DELETE /flight/{} cagirildi : ucus silinir", id);
        flightService.deleteFlight(id);
        log.info("silindi: Flight id={}", id);
        log.info("END: DELETE /flight/{} tamamlandi", id);
    }
}
