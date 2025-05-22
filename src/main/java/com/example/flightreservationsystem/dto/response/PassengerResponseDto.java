package com.example.flightreservationsystem.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PassengerResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private int age;
    private String gender;
    private LocalDateTime dateOfBirth;
    private String gmail;
}
