package com.example.flightreservationsystem.dto;

import com.example.flightreservationsystem.enums.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthDto {
    private String email;
    private String password;
    private Role role;
    private String token;
}
