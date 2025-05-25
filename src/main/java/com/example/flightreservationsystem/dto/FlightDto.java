package com.example.flightreservationsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FlightDto {

    private Long id;

    @NotBlank(message = "Flight number cannot be blank")
    private String flightNumber;

    @NotBlank(message = "Departure cannot be blank")
    private String departure;

    @NotBlank(message = "Destination cannot be blank")
    private String destination;

    @NotNull(message = "Departure time is required")
    private LocalDateTime departureTime;

    @NotNull(message = "Arrival time is required")
    private LocalDateTime arrivalTime;

    @Positive(message = "Price must be positive")
    private double price;


}
