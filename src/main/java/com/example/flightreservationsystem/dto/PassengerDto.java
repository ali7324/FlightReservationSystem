package com.example.flightreservationsystem.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PassengerDto {

    private Long id;

    @NotBlank(message = "First name cannot be blank.")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank.")
    private String lastName;

    @Min(value = 0, message = "Age must be at least 0.")
    @Max(value = 150, message = "Age must be less than or equal to 150.")
    private Integer age;

    @NotBlank(message = "Gender is required.")
    @Pattern(regexp = "Male|Female|Other", message = "Gender must be Male, Female, or Other.")
    private String gender;

    @NotNull(message = "Date of birth is required.")
    @Past(message = "Date of birth must be in the past.")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Email is required.")
    @Email(message = "Invalid email format.")
    private String email;

    private FlightDto flight;


}
