package com.example.flightreservationsystem.service;

import com.example.flightreservationsystem.dto.FlightDto;
import com.example.flightreservationsystem.dto.PassengerDto;
import com.example.flightreservationsystem.dto.PaymentDto;
import com.example.flightreservationsystem.entity.PaymentEntity;
import com.example.flightreservationsystem.entity.ReservationEntity;
import com.example.flightreservationsystem.enums.PaymentStatus;
import com.example.flightreservationsystem.enums.ReservationStatus;
import com.example.flightreservationsystem.exception.ResourceNotFoundException;
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
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with ID: " + dto.getReservationId()));

        PaymentStatus status = simulatePaymentStatus();
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

            try {
                PassengerDto passengerDto = passengerMapper.toDto(reservation.getPassenger());
                FlightDto flightDto = flightMapper.toDto(reservation.getFlight());
                mailService.sendReservationConfirmationMail(passengerDto, flightDto);
                log.info("Confirmation email sent to: {}", reservation.getPassenger().getGmail());
            } catch (Exception e) {
                log.error("Failed to send confirmation email: {}", e.getMessage());
            }

            return "Payment successful. Reservation confirmed and confirmation email sent.";
        }

        if (status == PaymentStatus.FAILED) {
            return "Payment failed. Please check your card details and try again.";
        }

        return "Payment is pending. Please wait for confirmation.";
    }

    private PaymentStatus simulatePaymentStatus() {
        return PaymentStatus.values()[new Random().nextInt(PaymentStatus.values().length)];
    }
}
