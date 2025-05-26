package com.example.flightreservationsystem.controller;

import com.example.flightreservationsystem.dto.FlightDto;
import com.example.flightreservationsystem.dto.ApiResponse;
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
    public ResponseEntity<ApiResponse<List<FlightDto>>> getAllFlights() {
        return ResponseEntity.ok(ApiResponse.success(flightService.getAllFlights()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FlightDto>> getFlightById(@PathVariable Long id) {
        FlightDto flight = flightService.getFlightOrThrow(id);
        return ResponseEntity.ok(ApiResponse.success(flight));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FlightDto>> addFlight(@RequestBody FlightDto flightDto) {
        FlightDto created = flightService.createFlight(flightDto);
        return ResponseEntity.ok(ApiResponse.success(created, "Flight created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FlightDto>> updateFlight(@PathVariable Long id,
                                                               @RequestBody FlightDto flightDto) {
        FlightDto updated = flightService.updateFlightOrThrow(id, flightDto);
        return ResponseEntity.ok(ApiResponse.success(updated, "Flight updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFlight(@PathVariable Long id) {
        flightService.deleteFlight(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Flight deleted successfully"));
    }
}
