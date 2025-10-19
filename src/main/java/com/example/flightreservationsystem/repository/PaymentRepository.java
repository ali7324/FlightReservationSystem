package com.example.flightreservationsystem.repository;

import com.example.flightreservationsystem.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    Optional<PaymentEntity> findByReservation_Id(Long reservationId);
}
