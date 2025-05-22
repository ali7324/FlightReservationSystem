package com.example.flightreservationsystem.controller;

import com.example.flightreservationsystem.dto.request.PassengerRequestDto;
import com.example.flightreservationsystem.dto.response.PassengerResponseDto;
import com.example.flightreservationsystem.entity.PassengerEntity;
import com.example.flightreservationsystem.service.PassengerService;
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
@RequestMapping("/passengers")
@RequiredArgsConstructor
public class PassengerController {

    private final PassengerService passengerService;

    @GetMapping
    public List<PassengerResponseDto> getAllPassengers() {
        log.info("START: GET /passengers cagirildi: butun sernisinler sorgulandi");
        List<PassengerResponseDto> passengers = passengerService.getAllPassengers();
        log.info("END: GET /passengers tamamlandı: {} sernisin tapıldı", passengers.size());
        return passengers;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PassengerResponseDto> getPassengerById(@PathVariable Long id) {
        log.info("START: GET /passengers/{} cagirildi", id);
        ResponseEntity<PassengerResponseDto> response = passengerService.getPassengerById(id)
                .map(passenger -> {
                    log.info("ugurla tapıldı: Passenger id={}", id);
                    log.info("END: GET /passengers/{} tamamlandı", id);
                    return ResponseEntity.ok(passenger);
                })
                .orElseGet(() -> {
                    log.warn("Passenger id={} tapılmadı", id);
                    log.info("END: GET /passengers/{} tamamlandı - tapılmadı", id);
                    return ResponseEntity.notFound().build();
                });
        return response;
    }

    @PostMapping
    public PassengerResponseDto addPassenger(@RequestBody PassengerRequestDto requestDto) {
        log.info("START: POST /passengers cagirildi: Yeni sernisin elave edilir: {}", requestDto);
        PassengerResponseDto created = passengerService.addPassenger(requestDto);
        log.info("ugurla elave edildi: Passenger id={}", created.getId());
        log.info("END: POST /passengers tamamlandı");
        return created;
    }

    @PutMapping("/{id}")
    public ResponseEntity<PassengerResponseDto> updatePassenger(@PathVariable Long id, @RequestBody PassengerRequestDto requestDto) {
        log.info("START: PUT /passengers/{} cagirildi: yenilenen melumatlar: {}", id, requestDto);
        PassengerResponseDto updated = passengerService.updatePassenger(id, requestDto);
        if (updated != null) {
            log.info("ugurla yenilendi: Passenger id={}", id);
            log.info("END: PUT /passengers/{} tamamlandı", id);
            return ResponseEntity.ok(updated);
        } else {
            log.warn("yenilenme ugursuz oldu: Passenger id={} tapılmadı", id);
            log.info("END: PUT /passengers/{} tamamlandı - tapılmadı", id);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public void deletePassenger(@PathVariable Long id) {
        log.info("START: DELETE /passengers/{} cagirildi: sernisin silinir", id);
        passengerService.deletePassenger(id);
        log.info("ugurla silindi: Passenger id={}", id);
        log.info("END: DELETE /passengers/{} tamamlandı", id);
    }
}
