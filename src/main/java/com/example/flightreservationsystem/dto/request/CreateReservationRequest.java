package com.example.flightreservationsystem.dto.request;

import com.example.flightreservationsystem.dto.PaymentDto;
import com.example.flightreservationsystem.dto.ReservationDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateReservationRequest {

    @Valid
    @NotNull
    private ReservationDto reservation;

    @Valid
    @NotNull
    private PaymentDto payment;
}
