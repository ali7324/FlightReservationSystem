package com.example.flightreservationsystem.repository;

import com.example.flightreservationsystem.entity.PassengerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PassengerRepository extends JpaRepository<PassengerEntity, Long> {
    Optional<PassengerEntity> findByEmail(String email);
}
