package com.example.flightreservationsystem.controller;

import com.example.flightreservationsystem.dto.PassengerDto;
import com.example.flightreservationsystem.dto.ApiResponse;
import com.example.flightreservationsystem.service.PassengerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/passengers")
@RequiredArgsConstructor
public class PassengerController {

    private final PassengerService passengerService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PassengerDto>>> getAllPassengers() {
        return ResponseEntity.ok(ApiResponse.success(passengerService.getAllPassengers()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PassengerDto>> getPassengerById(@PathVariable Long id) {
        PassengerDto passenger = passengerService.getPassengerOrThrow(id);
        return ResponseEntity.ok(ApiResponse.success(passenger, "Passenger retrieved successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PassengerDto>> addPassenger(@RequestBody PassengerDto requestDto) {
        PassengerDto created = passengerService.createPassenger(requestDto);
        return ResponseEntity.ok(ApiResponse.success(created, "Passenger created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PassengerDto>> updatePassenger(@PathVariable Long id, @RequestBody PassengerDto requestDto) {
        PassengerDto updated = passengerService.updatePassengerOrThrow(id, requestDto);
        return ResponseEntity.ok(ApiResponse.success(updated, "Passenger updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePassenger(@PathVariable Long id) {
        passengerService.deletePassenger(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Passenger deleted successfully"));
    }
}
