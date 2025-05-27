package com.example.flightreservationsystem.repository;

import com.example.flightreservationsystem.entity.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {

    @Query(value = "SELECT * FROM reservation r " +
            "JOIN flight f ON r.flight_id = f.id " +
            "WHERE DATE(f.departure_time) = CURRENT_DATE + INTERVAL 1 DAY " +
            "AND r.status = 'CONFIRMED'", nativeQuery = true)
    List<ReservationEntity> findTomorrowConfirmedReservations();
}
