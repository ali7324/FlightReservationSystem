package com.example.flightreservationsystem.validation;

import com.example.flightreservationsystem.dto.FlightDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDateTime;

@Slf4j
@Component
public class FlightValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return FlightDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        FlightDto dto = (FlightDto) target;

        // Flight Number
        if (dto.getFlightNumber() == null || dto.getFlightNumber().trim().isEmpty()) {
            errors.rejectValue("flightNumber", "Invalid.flightNumber", "Flight number cannot be blank.");
        }

        // Departure
        if (dto.getDeparture() == null || dto.getDeparture().trim().isEmpty()) {
            errors.rejectValue("departure", "Invalid.departure", "Departure location cannot be blank.");
        }

        // Destination
        if (dto.getDestination() == null || dto.getDestination().trim().isEmpty()) {
            errors.rejectValue("destination", "Invalid.destination", "Destination cannot be blank.");
        }

        // Departure Time
        if (dto.getDepartureTime() == null) {
            errors.rejectValue("departureTime", "Invalid.departureTime", "Departure time is required.");
        } else if (!dto.getDepartureTime().isAfter(LocalDateTime.now())) {
            errors.rejectValue("departureTime", "Invalid.departureTimeFuture", "Departure time must be in the future.");
        }

        // Arrival Time
        if (dto.getArrivalTime() == null) {
            errors.rejectValue("arrivalTime", "Invalid.arrivalTime", "Arrival time is required.");
        } else if (!dto.getArrivalTime().isAfter(LocalDateTime.now())) {
            errors.rejectValue("arrivalTime", "Invalid.arrivalTimeFuture", "Arrival time must be in the future.");
        }

        // Price
        if (dto.getPrice() <= 0) {
            errors.rejectValue("price", "Invalid.price", "Price must be a positive number.");
        }
    }
}
