package com.example.flightreservationsystem.controller;

import com.example.flightreservationsystem.dto.PassengerDto;
import com.example.flightreservationsystem.payload.ApiResponse;
import com.example.flightreservationsystem.service.PassengerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/passengers")
@RequiredArgsConstructor
public class PassengerController {


    private final PassengerService passengerService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PassengerDto>>> getAllPassengers() {
        List<PassengerDto> passengers = passengerService.getAllPassengers();
        return ResponseEntity.ok(ApiResponse.success(passengers));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PassengerDto>> getPassengerById(@PathVariable Long id) {
        return passengerService.getPassengerById(id)
                .map(passenger -> ResponseEntity.ok(ApiResponse.success(passenger, "Passenger retrieved successfully")))
                .orElseGet(() -> ResponseEntity
                        .status(404)
                        .body(ApiResponse.error("Passenger not found")));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PassengerDto>> addPassenger(@RequestBody PassengerDto requestDto) {
        PassengerDto created = passengerService.createPassenger(requestDto);
        return ResponseEntity.ok(ApiResponse.success(created, "Passenger created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PassengerDto>> updatePassenger(@PathVariable Long id, @RequestBody PassengerDto requestDto) {
        PassengerDto updated = passengerService.updatePassenger(id, requestDto);
        if (updated != null) {
            return ResponseEntity.ok(ApiResponse.success(updated, "Passenger updated successfully"));
        } else {
            return ResponseEntity.status(404).body(ApiResponse.error("Passenger not found"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePassenger(@PathVariable Long id) {
        passengerService.deletePassenger(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Passenger deleted successfully"));
    }
}
