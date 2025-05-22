package com.example.flightreservationsystem.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationRequestDto {
    @NotNull(message = "Flight ID is required")
    private Long flightId;

    @NotNull(message = "Passenger ID is required")
    private Long passengerId;

    @NotNull(message = "Reservation date is required")
    private LocalDateTime reservationDate;
}
