package com.example.flightreservationsystem.dto.request;

import com.example.flightreservationsystem.dto.FlightDto;
import com.example.flightreservationsystem.dto.PassengerDto;
import jakarta.validation.Valid;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MailTestRequest {
    @Valid
    private PassengerDto passenger;

    @Valid
    private FlightDto flight;
}
