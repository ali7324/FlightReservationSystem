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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
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

    @Transactional
    public String processPayment(PaymentDto dto) {
        log.info("Processing payment for reservationId={}", dto.getReservationId());

        ReservationEntity reservation = reservationRepository.findById(dto.getReservationId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reservation not found with ID: " + dto.getReservationId()));

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("Only PENDING reservations can be paid");
        }


        YearMonth expiry = parseExpiry(dto.getExpiryDate());
        if (expiry.isBefore(YearMonth.now())) {
            throw new IllegalArgumentException("Card expired");
        }


        BigDecimal amount = dto.getAmount();
        BigDecimal flightPrice = toBigDecimal(getFlightPriceNumber(reservation));
        if (amount == null || flightPrice == null || amount.compareTo(flightPrice) < 0) {
            throw new IllegalArgumentException("Amount is invalid or less than flight price");
        }

        PaymentStatus status = simulatePaymentStatus();
        log.info("Payment simulated result: {}", status);

        PaymentEntity payment = PaymentEntity.builder()
                .cardNumber(maskCard(dto.getCardNumber()))
                .cvv("***")
                .expiryDate(dto.getExpiryDate())
                .amount(amount)
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
                log.info("Confirmation email sent to {}", passengerDto.getEmail()); // << düzəliş
            } catch (Exception e) {
                log.warn("Email send failed after successful payment: {}", e.getMessage());
            }

            return "Payment successful. Reservation confirmed and confirmation email sent.";
        }

        if (status == PaymentStatus.FAILED) {
            reservation.setStatus(ReservationStatus.FAILED);
            reservationRepository.save(reservation);
            return "Payment failed. Please check your card details and try again.";
        }

        return "Payment is pending. Please wait for confirmation.";
    }




    PaymentStatus simulatePaymentStatus() {
        return PaymentStatus.values()[new Random().nextInt(PaymentStatus.values().length)];
    }

    private String maskCard(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) return "****";
        String last4 = cardNumber.substring(cardNumber.length() - 4);
        return "**** **** **** " + last4;
    }

    private YearMonth parseExpiry(String mmYY) {

        int month = Integer.parseInt(mmYY.substring(0, 2));
        int year = 2000 + Integer.parseInt(mmYY.substring(3, 5));
        return YearMonth.of(year, month);
    }


    private Number getFlightPriceNumber(ReservationEntity reservation) {
        return reservation.getFlight().getPrice();
    }


    private BigDecimal toBigDecimal(Number n) {
        if (n == null) return null;
        if (n instanceof BigDecimal bd) return bd;
        if (n instanceof Long || n instanceof Integer) return BigDecimal.valueOf(n.longValue());
        return BigDecimal.valueOf(n.doubleValue());
    }
}
