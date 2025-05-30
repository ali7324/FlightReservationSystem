package com.example.flightreservationsystem.dto;

import com.example.flightreservationsystem.enums.ReservationStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationDto {


    private Long id;

    @NotNull(message = "Flight ID is required.")
    private Long flightId;

    @NotNull(message = "Passenger ID is required.")
    private Long passengerId;

    @NotNull(message = "Reservation date is required.")
    @FutureOrPresent(message = "Reservation date cannot be in the past.")
    private LocalDateTime reservationDate;

    @NotNull(message = "Reservation status is required.")
    private ReservationStatus status;

    private FlightDto flight;
    private PassengerDto passenger;

}
