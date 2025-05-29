package com.example.flightreservationsystem.service;

import com.example.flightreservationsystem.dto.FlightDto;
import com.example.flightreservationsystem.dto.PassengerDto;
import com.example.flightreservationsystem.dto.PaymentDto;
import com.example.flightreservationsystem.entity.PaymentEntity;
import com.example.flightreservationsystem.entity.ReservationEntity;
import com.example.flightreservationsystem.enums.PaymentStatus;
import com.example.flightreservationsystem.enums.ReservationStatus;
import com.example.flightreservationsystem.mapper.FlightMapper;
import com.example.flightreservationsystem.mapper.PassengerMapper;
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
    private final MailService mailService;
    private final PassengerMapper passengerMapper;
    private final FlightMapper flightMapper;

    public String processPayment(PaymentDto dto) {
        log.info("Processing payment for reservation ID: {}", dto.getReservationId());

        ReservationEntity reservation = reservationRepository.findById(dto.getReservationId())
                .orElseThrow(() -> new RuntimeException("Reservation not found with ID: " + dto.getReservationId()));

        PaymentStatus status = decideRandomStatus();
        log.info("Simulated payment result: {}", status);

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

        if (status == PaymentStatus.SUCCESS) {
            reservation.setStatus(ReservationStatus.CONFIRMED);
            reservationRepository.save(reservation);

            PassengerDto passengerDto = passengerMapper.toDto(reservation.getPassenger());
            FlightDto flightDto = flightMapper.toDto(reservation.getFlight());
            mailService.sendReservationConfirmationMail(passengerDto, flightDto);

            return "Payment successful. Reservation confirmed and confirmation email sent.";
        } else if (status == PaymentStatus.FAILED) {
            return "Payment failed. Please check your card details and try again.";
        } else {
            return "Payment is pending. Please wait for confirmation.";
        }
    }

    private PaymentStatus decideRandomStatus() {
        int pick = new Random().nextInt(3);
        return PaymentStatus.values()[pick];
    }
}
