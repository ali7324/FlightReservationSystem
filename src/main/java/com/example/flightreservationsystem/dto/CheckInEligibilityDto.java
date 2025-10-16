// com.example.flightreservationsystem.dto.checkin.CheckInEligibilityDto
package com.example.flightreservationsystem.dto;


import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CheckInEligibilityDto {
    private boolean eligible;
    private String reason;
    private LocalDateTime windowStart;
    private LocalDateTime windowEnd;
}
