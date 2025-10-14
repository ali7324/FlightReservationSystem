package com.example.flightreservationsystem.repository;

import com.example.flightreservationsystem.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
}
