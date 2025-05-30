package com.example.flightreservationsystem.dto;

import jakarta.validation.constraints.Future;
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

    @NotBlank(message = "Flight number cannot be blank.")
    private String flightNumber;

    @NotBlank(message = "Departure location cannot be blank.")
    private String departure;

    @NotBlank(message = "Destination cannot be blank.")
    private String destination;

    @NotNull(message = "Departure time is required.")
    @Future(message = "Departure time must be in the future.")
    private LocalDateTime departureTime;

    @NotNull(message = "Arrival time is required.")
    @Future(message = "Arrival time must be in the future.")
    private LocalDateTime arrivalTime;

    @Positive(message = "Price must be a positive number.")
    private double price;

}
