package com.example.flightreservationsystem.dto;

import com.example.flightreservationsystem.enums.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

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

    // Register üçün tələb oluna bilər; login üçün zəruri deyil
    private Role role;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String token;
}
