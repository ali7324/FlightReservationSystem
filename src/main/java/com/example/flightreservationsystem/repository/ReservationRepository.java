package com.example.flightreservationsystem.repository;

import com.example.flightreservationsystem.entity.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {

    @Query(value = """
            SELECT r.* 
              FROM reservation r 
              JOIN flights f ON r.flight_id = f.id
             WHERE DATE(f.departure_time) = CURRENT_DATE + INTERVAL '1 day'
               AND r.status = 'CONFIRMED'
            """, nativeQuery = true)
    List<ReservationEntity> findTomorrowConfirmedReservations();
}
