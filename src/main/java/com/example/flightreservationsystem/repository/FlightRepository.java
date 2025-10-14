package com.example.flightreservationsystem.repository;

import com.example.flightreservationsystem.entity.FlightEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface FlightRepository extends JpaRepository<FlightEntity, Long> {

    @Query("""
           SELECT f FROM FlightEntity f
           WHERE (:flightNumber IS NULL OR LOWER(f.flightNumber) LIKE LOWER(CONCAT('%', :flightNumber, '%')))
             AND (:departure    IS NULL OR LOWER(f.departure) = LOWER(:departure))
             AND (:destination  IS NULL OR LOWER(f.destination) = LOWER(:destination))
             AND (:departureDate IS NULL OR FUNCTION('DATE', f.departureTime) = :departureDate)
           ORDER BY f.departureTime ASC
           """)
    List<FlightEntity> searchFlights(@Param("flightNumber") String flightNumber,
                                     @Param("departure") String departure,
                                     @Param("destination") String destination,
                                     @Param("departureDate") LocalDate departureDate);
}
