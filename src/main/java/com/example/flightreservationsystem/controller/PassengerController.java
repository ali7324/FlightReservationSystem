package com.example.flightreservationsystem.controller;

import com.example.flightreservationsystem.dto.ApiResponse;
import com.example.flightreservationsystem.dto.PassengerDto;
import com.example.flightreservationsystem.service.PassengerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/passengers")
@RequiredArgsConstructor
public class PassengerController {

    private final PassengerService passengerService;

    @GetMapping
    public ApiResponse<List<PassengerDto>> getAllPassengers() {
        return ApiResponse.ok("All passengers retrieved successfully", passengerService.getAllPassengers());
    }

    @GetMapping("/{id}")
    public ApiResponse<PassengerDto> getPassengerById(@PathVariable Long id) {
        return ApiResponse.ok("Passenger retrieved successfully", passengerService.getPassengerOrThrow(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ApiResponse<PassengerDto> addPassenger(@Valid @RequestBody PassengerDto requestDto) {
        return ApiResponse.ok("Passenger created successfully", passengerService.createPassenger(requestDto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ApiResponse<PassengerDto> updatePassenger(@PathVariable Long id,
                                                     @Valid @RequestBody PassengerDto requestDto) {
        return ApiResponse.ok("Passenger updated successfully", passengerService.updatePassengerOrThrow(id, requestDto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePassenger(@PathVariable Long id) {
        passengerService.deletePassenger(id);
        return ApiResponse.ok("Passenger deleted successfully", null);
    }
}
