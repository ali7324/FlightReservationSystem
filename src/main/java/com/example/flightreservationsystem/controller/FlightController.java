package com.example.flightreservationsystem.controller;

import com.example.flightreservationsystem.entity.FlightEntity;
import com.example.flightreservationsystem.service.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/flight")
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;

    @GetMapping
    public List<FlightEntity> getAllFlights() {
        return flightService.getAllFlights();
    }

    public ResponseEntity<FlightEntity> getFlightById(@PathVariable Long id) {
        return flightService.getFlightById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public FlightEntity addFlight(@RequestBody FlightEntity flight) {
        return flightService.addFlight(flight);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FlightEntity> updateFlight(@PathVariable Long id, @RequestBody FlightEntity flight) {
        FlightEntity updatedFlight = flightService.updateFlight(id, flight);
        if (updatedFlight != null) {
            return ResponseEntity.ok(updatedFlight);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public void deleteFlight(@PathVariable Long id) {
        flightService.deleteFlight(id);
    }
}
