package com.example.flightreservationsystem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentDto {

    @NotBlank(message = "Card number is required.")
    @Pattern(regexp = "\\d{16}", message = "Card number must be exactly 16 digits.")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String cardNumber;

    @NotBlank(message = "CVV is required.")
    @Pattern(regexp = "\\d{3}", message = "CVV must be exactly 3 digits.")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String cvv;

    @NotBlank(message = "Expiry date is required.")
    @Pattern(regexp = "^(0[1-9]|1[0-2])/\\d{2}$", message = "Expiry date must be in MM/YY format.")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String expiryDate;

    @NotNull(message = "Amount is required.")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0.")
    private BigDecimal amount;

    @NotNull(message = "Reservation ID is required.")
    private Long reservationId;

    // NOTE: Service qatında: expiryDate VAQTLARINI yoxla (bitmiş olmasın)
}
