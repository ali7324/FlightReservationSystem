package com.example.flightreservationsystem.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FlightDto {

    private Long id;

    @NotBlank(message = "Flight number cannot be blank.")
    @Pattern(regexp = "^[A-Z0-9]{2,3}\\d{2,4}$",
            message = "Flight number must look like TK0901 or AA123")
    private String flightNumber;

    @NotBlank(message = "Departure location cannot be blank.")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Departure must be IATA 3-letter code (e.g., GYD)")
    private String departure;

    @NotBlank(message = "Destination cannot be blank.")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Destination must be IATA 3-letter code (e.g., IST)")
    private String destination;

    @NotNull(message = "Departure time is required.")
    @Future(message = "Departure time must be in the future.")
    private LocalDateTime departureTime;

    @NotNull(message = "Arrival time is required.")
    @Future(message = "Arrival time must be in the future.")
    private LocalDateTime arrivalTime;

    @NotNull(message = "Price is required.")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;

    // NOTE: Service qatında: arrivalTime > departureTime olmalıdır (biznes qaydası)
}
