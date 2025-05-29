package com.example.flightreservationsystem.validation;

import com.example.flightreservationsystem.dto.ReservationDto;
import com.example.flightreservationsystem.enums.ReservationStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDateTime;

@Slf4j
@Component
public class ReservationValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return ReservationDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ReservationDto dto = (ReservationDto) target;

        // Flight ID
        if (dto.getFlightId() == null) {
            errors.rejectValue("flightId", "Invalid.flightId", "Flight ID is required.");
        }

        // Passenger ID
        if (dto.getPassengerId() == null) {
            errors.rejectValue("passengerId", "Invalid.passengerId", "Passenger ID is required.");
        }

        // Reservation Date
        if (dto.getReservationDate() == null) {
            errors.rejectValue("reservationDate", "Invalid.reservationDate", "Reservation date is required.");
        } else if (dto.getReservationDate().isBefore(LocalDateTime.now())) {
            errors.rejectValue("reservationDate", "Invalid.reservationDatePast", "Reservation date cannot be in the past.");
        }

        // Reservation Status
        if (dto.getStatus() == null) {
            errors.rejectValue("status", "Invalid.status", "Reservation status is required.");
        } else if (!isValidStatus(dto.getStatus())) {
            errors.rejectValue("status", "Invalid.statusValue", "Invalid reservation status.");
        }
    }

    private boolean isValidStatus(ReservationStatus status) {
        return switch (status) {
            case PENDING, CONFIRMED, CANCELLED -> true;
        };
    }
}
