package com.example.flightreservationsystem.controller;

import com.example.flightreservationsystem.entity.PassengerEntity;
import com.example.flightreservationsystem.service.PassengerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/passengers")
@RequiredArgsConstructor
public class PassengerController {

    private final PassengerService passengerService;


    @GetMapping
    public List<PassengerEntity> getAllPassengers() {
        return passengerService.getAllPassengers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PassengerEntity> getPassengerById(@PathVariable Long id) {
        return passengerService.getPassengerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PassengerEntity> addPassenger(@RequestBody PassengerEntity passenger) {
        return ResponseEntity.ok(passengerService.addPassenger(passenger));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PassengerEntity> updatePassenger(@PathVariable Long id, @RequestBody PassengerEntity passenger) {
        PassengerEntity updatedPassenger = passengerService.updatePassenger(id, passenger);
        if (updatedPassenger != null) {
            return ResponseEntity.ok(updatedPassenger);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public void deletePassenger(@PathVariable Long id) {
        passengerService.deletePassenger(id);
    }
}
