package com.example.flightreservationsystem.validation;

import com.example.flightreservationsystem.dto.PaymentDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.math.BigDecimal;

@Slf4j
@Component
public class PaymentValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return PaymentDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PaymentDto dto = (PaymentDto) target;

        if (dto.getCardNumber() == null || !dto.getCardNumber().matches("\\d{16}")) {
            errors.rejectValue("cardNumber", "Invalid.cardNumber", "Card number must be exactly 16 digits.");
        }

        if (dto.getCvv() == null || !dto.getCvv().matches("\\d{3}")) {
            errors.rejectValue("cvv", "Invalid.cvv", "CVV must be exactly 3 digits.");
        }

        if (dto.getExpiryDate() == null || !dto.getExpiryDate().matches("^(0[1-9]|1[0-2])/\\d{2}$")) {
            errors.rejectValue("expiryDate", "Invalid.expiryDate", "Expiry date must be in MM/YY format.");
        }

        if (dto.getAmount() == null || dto.getAmount().compareTo(new BigDecimal("0.01")) < 0) {
            errors.rejectValue("amount", "Invalid.amount", "Amount must be greater than 0.");
        }

        if (dto.getReservationId() == null) {
            errors.rejectValue("reservationId", "Invalid.reservationId", "Reservation ID is required.");
        }
    }
}
