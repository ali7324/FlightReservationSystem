package com.example.flightreservationsystem.controller;

import com.example.flightreservationsystem.dto.FlightDto;
import com.example.flightreservationsystem.dto.ApiResponse;
import com.example.flightreservationsystem.service.FlightService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
    public ResponseEntity<ApiResponse<FlightDto>> addFlight(@Valid @RequestBody FlightDto flightDto) {
        FlightDto created = flightService.createFlight(flightDto);
        return ResponseEntity.ok(ApiResponse.success(created, "Flight created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FlightDto>> updateFlight(
            @PathVariable Long id,
            @Valid @RequestBody FlightDto flightDto
    ) {
        FlightDto updated = flightService.updateFlightOrThrow(id, flightDto);
        return ResponseEntity.ok(ApiResponse.success(updated, "Flight updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFlight(@PathVariable Long id) {
        flightService.deleteFlight(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Flight deleted successfully"));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<FlightDto>>> searchFlights(
            @RequestParam(required = false) String flightNumber,
            @RequestParam(required = false) String departure,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate departureDate
    ) {
        List<FlightDto> results = flightService.searchFlights(flightNumber, departure, destination, departureDate);
        return ResponseEntity.ok(ApiResponse.success(results, "Filtered flight results"));
    }
}
