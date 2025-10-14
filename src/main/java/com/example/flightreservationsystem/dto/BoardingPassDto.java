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
    private String boardingPassCode;  // məsələn: BP-123456-AB7XQ2
}
