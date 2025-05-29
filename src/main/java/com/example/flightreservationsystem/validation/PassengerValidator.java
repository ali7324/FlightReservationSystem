package com.example.flightreservationsystem.validation;

import com.example.flightreservationsystem.dto.PassengerDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDateTime;

@Slf4j
@Component
public class PassengerValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return PassengerDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PassengerDto dto = (PassengerDto) target;

        // First Name
        if (dto.getFirstName() == null || dto.getFirstName().trim().isEmpty()) {
            errors.rejectValue("firstName", "Invalid.firstName", "First name cannot be blank.");
        }

        // Last Name
        if (dto.getLastName() == null || dto.getLastName().trim().isEmpty()) {
            errors.rejectValue("lastName", "Invalid.lastName", "Last name cannot be blank.");
        }

        // Age
        if (dto.getAge() < 0 || dto.getAge() > 150) {
            errors.rejectValue("age", "Invalid.age", "Age must be between 0 and 150.");
        }

        // Gender
        if (dto.getGender() == null || !dto.getGender().matches("Male|Female|Other")) {
            errors.rejectValue("gender", "Invalid.gender", "Gender must be Male, Female, or Other.");
        }

        // Date of Birth
        if (dto.getDateOfBirth() == null) {
            errors.rejectValue("dateOfBirth", "Invalid.dateOfBirth", "Date of birth is required.");
        } else if (!dto.getDateOfBirth().isBefore(LocalDateTime.now())) {
            errors.rejectValue("dateOfBirth", "Invalid.dateOfBirth", "Date of birth must be in the past.");
        }

        // Email
        if (dto.getGmail() == null || dto.getGmail().trim().isEmpty()) {
            errors.rejectValue("gmail", "Invalid.gmail", "Email is required.");
        } else if (!dto.getGmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            errors.rejectValue("gmail", "Invalid.gmailFormat", "Invalid email format.");
        }
    }
}
