package com.example.flightreservationsystem.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PassengerDto {

    private Long id;
    private String firstName;
    private String lastName;
    private int age;
    private String gender;
    private LocalDateTime dateOfBirth;
    private String gmail;
    private FlightDto flight;
}
