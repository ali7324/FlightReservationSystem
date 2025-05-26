package com.example.flightreservationsystem.repository;

import com.example.flightreservationsystem.entity.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {

    @Query("SELECT r FROM ReservationEntity r " +
            "WHERE DATE(r.flight.departureTime) = CURRENT_DATE + 1 " +
            "AND r.status = 'CONFIRMED'")
    List<ReservationEntity> findTomorrowConfirmedReservations();

}
