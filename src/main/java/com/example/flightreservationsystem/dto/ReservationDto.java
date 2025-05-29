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
    private Long flightId;
    private Long passengerId;
    private LocalDateTime reservationDate;
    private ReservationStatus status;
    private FlightDto flight;
    private PassengerDto passenger;

}
