package com.example.flightreservationsystem.controller;

import com.example.flightreservationsystem.dto.FlightDto;
import com.example.flightreservationsystem.dto.ApiResponse;
import com.example.flightreservationsystem.service.FlightService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/flights")
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;

    @GetMapping
    public ApiResponse<List<FlightDto>> getAllFlights() {
        return ApiResponse.ok("All flights retrieved successfully", flightService.getAllFlights());
    }

    @GetMapping("/{id}")
    public ApiResponse<FlightDto> getFlightById(@PathVariable Long id) {
        return ApiResponse.ok("Flight retrieved successfully", flightService.getFlightOrThrow(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ApiResponse<FlightDto> addFlight(@Valid @RequestBody FlightDto flightDto) {
        return ApiResponse.ok("Flight created successfully", flightService.createFlight(flightDto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ApiResponse<FlightDto> updateFlight(@PathVariable Long id,
                                               @Valid @RequestBody FlightDto flightDto) {
        return ApiResponse.ok("Flight updated successfully", flightService.updateFlightOrThrow(id, flightDto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteFlight(@PathVariable Long id) {
        flightService.deleteFlight(id);
        return ApiResponse.ok("Flight deleted successfully", null);
    }

    @GetMapping("/search")
    public ApiResponse<List<FlightDto>> searchFlights(
            @RequestParam(required = false) String flightNumber,
            @RequestParam(required = false) String departure,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate departureDate) {
        return ApiResponse.ok("Filtered flight results retrieved successfully",
                flightService.searchFlights(flightNumber, departure, destination, departureDate));
    }
}
