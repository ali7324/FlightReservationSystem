package com.example.flightreservationsystem.controller;

import com.example.flightreservationsystem.dto.PassengerDto;
import com.example.flightreservationsystem.dto.ApiResponse;
import com.example.flightreservationsystem.service.PassengerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/passengers")
@RequiredArgsConstructor
public class PassengerController {

    private final PassengerService passengerService;

    @GetMapping
    public ApiResponse<List<PassengerDto>> getAllPassengers() {
        try {
            List<PassengerDto> passengers = passengerService.getAllPassengers();
            return ApiResponse.success("All passengers retrieved successfully", passengers);
        } catch (Exception e) {
            return ApiResponse.error("Error retrieving passengers: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<PassengerDto> getPassengerById(@PathVariable Long id) {
        try {
            PassengerDto passenger = passengerService.getPassengerOrThrow(id);
            return ApiResponse.success("Passenger retrieved successfully", passenger);
        } catch (Exception e) {
            return ApiResponse.error("Error retrieving passenger: " + e.getMessage());
        }
    }

    @PostMapping
    public ApiResponse<PassengerDto> addPassenger(@Valid @RequestBody PassengerDto requestDto) {
        try {
            PassengerDto created = passengerService.createPassenger(requestDto);
            return ApiResponse.success("Passenger created successfully", created);
        } catch (Exception e) {
            return ApiResponse.error("Error creating passenger: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResponse<PassengerDto> updatePassenger(
            @PathVariable Long id,
            @Valid @RequestBody PassengerDto requestDto
    ) {
        try {
            PassengerDto updated = passengerService.updatePassengerOrThrow(id, requestDto);
            return ApiResponse.success("Passenger updated successfully", updated);
        } catch (Exception e) {
            return ApiResponse.error("Error updating passenger: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePassenger(@PathVariable Long id) {
        try {
            passengerService.deletePassenger(id);
            return ApiResponse.success("Passenger deleted successfully", null);
        } catch (Exception e) {
            return ApiResponse.error("Error deleting passenger: " + e.getMessage());
        }
    }
}