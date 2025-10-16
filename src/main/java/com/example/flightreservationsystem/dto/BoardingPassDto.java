// com.example.flightreservationsystem.dto.checkin.BoardingPassDto
package com.example.flightreservationsystem.dto;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardingPassDto {
    private Long reservationId;
    private String boardingPassCode;
}
