// com.example.flightreservationsystem.service.CheckInService
package com.example.flightreservationsystem.service;

import com.example.flightreservationsystem.dto.BoardingPassDto;
import com.example.flightreservationsystem.dto.CheckInEligibilityDto;
import com.example.flightreservationsystem.entity.ReservationEntity;
import com.example.flightreservationsystem.enums.ReservationStatus;
import com.example.flightreservationsystem.exception.ResourceNotFoundException;
import com.example.flightreservationsystem.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckInService {

    private final ReservationRepository reservationRepository;
    private static final SecureRandom RNG = new SecureRandom();

    public CheckInEligibilityDto checkEligibility(Long reservationId) {
        ReservationEntity r = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with ID: " + reservationId));

        LocalDateTime dep = r.getFlight().getDepartureTime();
        LocalDateTime windowStart = dep.minusHours(24);
        LocalDateTime windowEnd = dep;
        LocalDateTime now = LocalDateTime.now();

        if (r.getStatus() != ReservationStatus.CONFIRMED) {
            return CheckInEligibilityDto.builder()
                    .eligible(false)
                    .reason("Only CONFIRMED reservations can be checked-in")
                    .windowStart(windowStart)
                    .windowEnd(windowEnd)
                    .build();
        }
        if (now.isBefore(windowStart)) {
            return CheckInEligibilityDto.builder()
                    .eligible(false)
                    .reason("Check-in window not opened yet")
                    .windowStart(windowStart)
                    .windowEnd(windowEnd)
                    .build();
        }
        if (now.isAfter(windowEnd)) {
            return CheckInEligibilityDto.builder()
                    .eligible(false)
                    .reason("Flight departure time passed")
                    .windowStart(windowStart)
                    .windowEnd(windowEnd)
                    .build();
        }
        if (r.getStatus() == ReservationStatus.CHECKED_IN) {
            return CheckInEligibilityDto.builder()
                    .eligible(false)
                    .reason("Already checked-in")
                    .windowStart(windowStart)
                    .windowEnd(windowEnd)
                    .build();
        }

        return CheckInEligibilityDto.builder()
                .eligible(true)
                .reason("OK")
                .windowStart(windowStart)
                .windowEnd(windowEnd)
                .build();
    }

    public BoardingPassDto checkIn(Long reservationId) {
        CheckInEligibilityDto eligibility = checkEligibility(reservationId);
        if (!eligibility.isEligible()) {
            throw new IllegalStateException("Not eligible for check-in: " + eligibility.getReason());
        }

        ReservationEntity r = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with ID: " + reservationId));

        r.setStatus(ReservationStatus.CHECKED_IN);
        reservationRepository.save(r);

        String code = generateBoardingPass(reservationId);
        log.info("Reservation {} checked-in. BP={}", reservationId, code);

        return BoardingPassDto.builder()
                .reservationId(reservationId)
                .boardingPassCode(code)
                .build();
    }

    private String generateBoardingPass(Long reservationId) {

        String alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) sb.append(alphabet.charAt(RNG.nextInt(alphabet.length())));
        return "BP-" + reservationId + "-" + sb;
    }
}
