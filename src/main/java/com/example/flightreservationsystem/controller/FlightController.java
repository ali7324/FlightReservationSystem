package com.example.flightreservationsystem.controller;

import com.example.flightreservationsystem.dto.FlightDto;
import com.example.flightreservationsystem.dto.ApiResponse;
import com.example.flightreservationsystem.service.FlightService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/flight")
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;

    @GetMapping
    public ApiResponse<List<FlightDto>> getAllFlights() {
        try {
            List<FlightDto> flights = flightService.getAllFlights();
            return ApiResponse.success("All flights retrieved successfully", flights);
        } catch (Exception e) {
            return ApiResponse.error("Error retrieving flights: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<FlightDto> getFlightById(@PathVariable Long id) {
        try {
            FlightDto flight = flightService.getFlightOrThrow(id);
            return ApiResponse.success("Flight retrieved successfully", flight);
        } catch (Exception e) {
            return ApiResponse.error("Error retrieving flight: " + e.getMessage());
        }
    }

    @PostMapping
    public ApiResponse<FlightDto> addFlight(@Valid @RequestBody FlightDto flightDto) {
        try {
            FlightDto created = flightService.createFlight(flightDto);
            return ApiResponse.success("Flight created successfully", created);
        } catch (Exception e) {
            return ApiResponse.error("Error creating flight: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResponse<FlightDto> updateFlight(
            @PathVariable Long id,
            @Valid @RequestBody FlightDto flightDto
    ) {
        try {
            FlightDto updated = flightService.updateFlightOrThrow(id, flightDto);
            return ApiResponse.success("Flight updated successfully", updated);
        } catch (Exception e) {
            return ApiResponse.error("Error updating flight: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteFlight(@PathVariable Long id) {
        try {
            flightService.deleteFlight(id);
            return ApiResponse.success("Flight deleted successfully", null);
        } catch (Exception e) {
            return ApiResponse.error("Error deleting flight: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    public ApiResponse<List<FlightDto>> searchFlights(
            @RequestParam(required = false) String flightNumber,
            @RequestParam(required = false) String departure,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate departureDate
    ) {
        try {
            List<FlightDto> results = flightService.searchFlights(flightNumber, departure, destination, departureDate);
            return ApiResponse.success("Filtered flight results retrieved successfully", results);
        } catch (Exception e) {
            return ApiResponse.error("Error searching flights: " + e.getMessage());
        }
    }
}