package com.example.flightreservationsystem.dto;

import com.example.flightreservationsystem.enums.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthDto {


    @NotBlank(message = "Email cannot be blank.")
    @Email(message = "Email should be valid.")
    private String email;

    @NotBlank(message = "Password cannot be blank.")
    @Size(min = 6, message = "Password must be at least 6 characters long.")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private Role role;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String token;


    @NotBlank(message = "First name is required.")
    @Size(max = 80, message = "First name is too long.")
    private String firstName;

    @NotBlank(message = "Last name is required.")
    @Size(max = 80, message = "Last name is too long.")
    private String lastName;

    @Min(value = 0, message = "Age cannot be negative.")
    private int age;

    @NotBlank(message = "Gender is required.")
    @Size(max = 10, message = "Gender is too long.")
    private String gender;

    @NotNull(message = "Date of birth is required.")
    private LocalDate dateOfBirth;
}
