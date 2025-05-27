package com.example.flightreservationsystem.service;

import com.example.flightreservationsystem.dto.PaymentDto;
import com.example.flightreservationsystem.entity.PaymentEntity;
import com.example.flightreservationsystem.entity.ReservationEntity;
import com.example.flightreservationsystem.enums.PaymentStatus;
import com.example.flightreservationsystem.repository.PaymentRepository;
import com.example.flightreservationsystem.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;

    public String processPayment(PaymentDto dto) {
        log.info("Starting payment process");

        ReservationEntity reservation = reservationRepository.findById(dto.getReservationId())
                .orElseThrow(() -> {
                    log.error("Reservation not found: ID={}", dto.getReservationId());
                    return new RuntimeException("Reservation not found");
                });

        PaymentStatus status = decideRandomStatus();
        log.info("Payment status: {}", status);

        PaymentEntity payment = PaymentEntity.builder()
                .cardNumber(dto.getCardNumber())
                .cvv(dto.getCvv())
                .expiryDate(dto.getExpiryDate())
                .amount(dto.getAmount())
                .status(status)
                .reservation(reservation)
                .createdAt(LocalDateTime.now())
                .build();

        paymentRepository.save(payment);
        log.info("Payment saved: ID={}, Status={}", payment.getId(), status);

        String message = switch (status) {
            case SUCCESS -> "Payment successful";
            case FAILED -> "Payment failed";
            case PENDING -> "Payment pending...";
        };

        log.info("Returning message: {}", message);
        return message;
    }

    private PaymentStatus decideRandomStatus() {
        int pick = new Random().nextInt(3);
        return PaymentStatus.values()[pick];
    }
}
