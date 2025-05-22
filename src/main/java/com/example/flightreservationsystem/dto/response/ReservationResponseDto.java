package com.example.flightreservationsystem.dto.response;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationResponseDto {
    private Long id;
    private FlightResponseDto flight;
    private PassengerResponseDto passenger;
    private LocalDateTime reservationDate;
}
