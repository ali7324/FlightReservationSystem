package com.example.flightreservationsystem.repository;

import com.example.flightreservationsystem.entity.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {

    // PostgreSQL-də sabit işləyən variant: ::date cast + INTERVAL '1 day'
    @Query(value = """
            SELECT r.*
              FROM reservation r
              JOIN flights f ON r.flight_id = f.id
             WHERE f.departure_time::date = CURRENT_DATE + INTERVAL '1 day'
               AND r.status = 'CONFIRMED'
            """, nativeQuery = true)
    List<ReservationEntity> findTomorrowConfirmedReservations();
}
