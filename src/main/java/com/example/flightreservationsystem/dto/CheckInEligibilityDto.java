// com.example.flightreservationsystem.dto.checkin.CheckInEligibilityDto
package com.example.flightreservationsystem.dto;


import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CheckInEligibilityDto {
    private boolean eligible;
    private String reason;                // eligible=false olduqda səbəb
    private LocalDateTime windowStart;    // check-in pəncərəsinin başlanğıcı (departure-24h)
    private LocalDateTime windowEnd;      // check-in pəncərəsinin sonu (departure)
}
