package com.example.flightreservationsystem.dto;

import com.example.flightreservationsystem.enums.ReservationStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime reservationDate;

    @NotNull(message = "Reservation status is required.")
    private ReservationStatus status;

    private FlightDto flight;
    private PassengerDto passenger;

    // NOTE: Service qatında: flight mövcudluğu, seat availability,
    // overbooking yoxlaması kimi biznes qaydaları
}
