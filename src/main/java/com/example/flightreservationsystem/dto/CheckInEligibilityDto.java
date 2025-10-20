// Controller/Front üçün aydın cavab modeli:
// eligible? yoxdursa niyə (reason), pəncərə nə vaxtdan-nə vaxta (windowStart/End)
package com.example.flightreservationsystem.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CheckInEligibilityDto {
    private boolean eligible;           // check-in mümkündür?
    private String reason;              // mümkün deyilsə səbəb
    private LocalDateTime windowStart;  // pəncərənin başlanğıcı
    private LocalDateTime windowEnd;    // pəncərənin sonu
}
