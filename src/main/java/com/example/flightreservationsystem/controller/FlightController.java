package com.example.flightreservationsystem.controller;

import com.example.flightreservationsystem.dto.FlightDto;
import com.example.flightreservationsystem.payload.ApiResponse;
import com.example.flightreservationsystem.service.FlightService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public ResponseEntity<ApiResponse<List<FlightDto>>> getAllFlights() {
        List<FlightDto> flights = flightService.getAllFlights();
        return ResponseEntity.ok(ApiResponse.success(flights));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FlightDto>> getFlightById(@PathVariable Long id) {
        return flightService.getFlightById(id)
                .map(flight -> ResponseEntity.ok(ApiResponse.success(flight)))
                .orElseGet(() -> ResponseEntity.status(404).body(ApiResponse.error("Flight not found")));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FlightDto>> addFlight(@RequestBody FlightDto flightDto) {
        FlightDto created = flightService.createFlight(flightDto);
        return ResponseEntity.ok(ApiResponse.success(created, "Flight created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FlightDto>> updateFlight(@PathVariable Long id,
                                                               @RequestBody FlightDto flightDto) {
        FlightDto updated = flightService.updateFlight(id, flightDto);
        if (updated != null) {
            return ResponseEntity.ok(ApiResponse.success(updated, "Flight updated successfully"));
        }
        return ResponseEntity.status(404).body(ApiResponse.error("Flight not found"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFlight(@PathVariable Long id) {
        flightService.deleteFlight(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Flight deleted successfully"));
    }
}
