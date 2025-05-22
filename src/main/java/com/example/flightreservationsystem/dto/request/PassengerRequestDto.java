package com.example.flightreservationsystem.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PassengerRequestDto {
    @NotBlank(message = "First name cannot be blank")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    private String lastName;

    @Min(value = 0, message = "Age cannot be negative")
    private int age;

    @NotBlank(message = "Gender cannot be blank")
    private String gender;

    @NotNull(message = "Date of birth is required")
    private LocalDateTime dateOfBirth;

    @NotBlank(message = "Gmail cannot be blank")
    private String gmail;
}
