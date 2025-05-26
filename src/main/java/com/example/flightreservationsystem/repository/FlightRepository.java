package com.example.flightreservationsystem.repository;

import com.example.flightreservationsystem.entity.FlightEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FlightRepository extends JpaRepository<FlightEntity, Long> {

    @Query("SELECT f FROM FlightEntity f " +
            "WHERE (:flightNumber IS NULL OR LOWER(f.flightNumber) LIKE LOWER(CONCAT('%', :flightNumber, '%'))) " +
            "AND (:departure IS NULL OR LOWER(f.departure) = LOWER(:departure)) " +
            "AND (:destination IS NULL OR LOWER(f.destination) = LOWER(:destination)) " +
            "AND (:departureDate IS NULL OR DATE(f.departureTime) = :departureDate)")
    List<FlightEntity> searchFlights(String flightNumber, String departure, String destination, LocalDate departureDate);

}
