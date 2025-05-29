package com.example.flightreservationsystem.controller;

import com.example.flightreservationsystem.dto.PassengerDto;
import com.example.flightreservationsystem.dto.ApiResponse;
import com.example.flightreservationsystem.service.PassengerService;
import com.example.flightreservationsystem.validation.PassengerValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/passengers")
@RequiredArgsConstructor
public class PassengerController {

    private final PassengerService passengerService;
    private final PassengerValidator passengerValidator;

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
    public ResponseEntity<ApiResponse<PassengerDto>> addPassenger(@RequestBody PassengerDto requestDto, Errors errors) {
        passengerValidator.validate(requestDto, errors);

        if (errors.hasErrors()) {
            String errorMsg = errors.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage())
                    .collect(Collectors.joining("; "));
            return ResponseEntity.badRequest().body(ApiResponse.error(errorMsg));
        }

        PassengerDto created = passengerService.createPassenger(requestDto);
        return ResponseEntity.ok(ApiResponse.success(created, "Passenger created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PassengerDto>> updatePassenger(@PathVariable Long id,
                                                                     @RequestBody PassengerDto requestDto,
                                                                     Errors errors) {
        passengerValidator.validate(requestDto, errors);

        if (errors.hasErrors()) {
            String errorMsg = errors.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage())
                    .collect(Collectors.joining("; "));
            return ResponseEntity.badRequest().body(ApiResponse.error(errorMsg));
        }

        PassengerDto updated = passengerService.updatePassengerOrThrow(id, requestDto);
        return ResponseEntity.ok(ApiResponse.success(updated, "Passenger updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePassenger(@PathVariable Long id) {
        passengerService.deletePassenger(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Passenger deleted successfully"));
    }
}
