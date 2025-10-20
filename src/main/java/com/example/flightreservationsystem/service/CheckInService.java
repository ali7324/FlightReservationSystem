// Check-in məntiqini daşıyan servis.
// Niyə var? Uçuşdan əvvəl sərnişinin onlayn qeydiyyata düşə bilməsi üçün
// qaydaları tətbiq edir: (1) yalnız CONFIRMED rezervasiya, (2) vaxt pəncərəsi
// açılıb-açılmadığı, (3) reysin vaxtı keçməyib, (4) artıq check-in olunmayıb.
// Uğurlu olduqda rezervasiyanın statusunu CHECKED_IN edir və "boarding pass" kodu qaytarır.

package com.example.flightreservationsystem.service;

import com.example.flightreservationsystem.dto.BoardingPassDto;      // nəticədə qaytarılan "boarding pass" məlumatı
import com.example.flightreservationsystem.dto.CheckInEligibilityDto; // uyğunluq (eligible?) cavabı
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

    private final ReservationRepository reservationRepository; // DB-dən rezervasiya oxumaq/yazmaq üçün
    private static final SecureRandom RNG = new SecureRandom(); // Boarding pass kodu üçün random generator

    // Check-in pəncərəsi və status qaydalarını hesablayır.
    public CheckInEligibilityDto checkEligibility(Long reservationId) {
        // 1) Rezervasiyanı tap: yoxdursa 404
        ReservationEntity r = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with ID: " + reservationId));

        // 2) Uçuş vaxtından 24 saat əvvəl pəncərə açılır, uçuş vaxtına qədər davam edir
        LocalDateTime dep = r.getFlight().getDepartureTime();
        LocalDateTime windowStart = dep.minusHours(24);
        LocalDateTime windowEnd = dep;
        LocalDateTime now = LocalDateTime.now();

        // 3) Yalnız CONFIRMED rezervasiya check-in edə bilər
        if (r.getStatus() != ReservationStatus.CONFIRMED) {
            return CheckInEligibilityDto.builder()
                    .eligible(false)
                    .reason("Only CONFIRMED reservations can be checked-in")
                    .windowStart(windowStart)
                    .windowEnd(windowEnd)
                    .build();
        }
        // 4) Pəncərə hələ açılmayıbsa (uçuşa 24 saatdan çox var)
        if (now.isBefore(windowStart)) {
            return CheckInEligibilityDto.builder()
                    .eligible(false)
                    .reason("Check-in window not opened yet")
                    .windowStart(windowStart)
                    .windowEnd(windowEnd)
                    .build();
        }
        // 5) Uçuş vaxtı keçibsə
        if (now.isAfter(windowEnd)) {
            return CheckInEligibilityDto.builder()
                    .eligible(false)
                    .reason("Flight departure time passed")
                    .windowStart(windowStart)
                    .windowEnd(windowEnd)
                    .build();
        }
        // 6) Təhlükəsizlik üçün ayrıca yoxlama: artıq check-in olunubsa
        if (r.getStatus() == ReservationStatus.CHECKED_IN) {
            return CheckInEligibilityDto.builder()
                    .eligible(false)
                    .reason("Already checked-in")
                    .windowStart(windowStart)
                    .windowEnd(windowEnd)
                    .build();
        }

        // 7) Hər şey qaydasındadır
        return CheckInEligibilityDto.builder()
                .eligible(true)
                .reason("OK")
                .windowStart(windowStart)
                .windowEnd(windowEnd)
                .build();
    }

    // Real check-in əməliyyatı: əvvəl uyğunluğu yoxlayır, sonra statusu CHECKED_IN edir,
    // və boarding pass kodu yaradır.
    @Transactional // (opsional, yaxşı praktikadır: status dəyişikliyi + save atomik olsun)
    public BoardingPassDto checkIn(Long reservationId) {
        // 1) Qaydalar pozularsa, istisna atırıq (controller 400/409 kimi çevirir)
        CheckInEligibilityDto eligibility = checkEligibility(reservationId);
        if (!eligibility.isEligible()) {
            throw new IllegalStateException("Not eligible for check-in: " + eligibility.getReason());
        }

        // 2) Rezervasiyanı yenidən tap və statusu CHECKED_IN et
        ReservationEntity r = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with ID: " + reservationId));

        r.setStatus(ReservationStatus.CHECKED_IN);
        reservationRepository.save(r);

        // 3) Sərnişinə veriləcək boarding pass kodu
        String code = generateBoardingPass(reservationId);
        log.info("Reservation {} checked-in. BP={}", reservationId, code);

        // 4) Müştəriyə qaytarılan DTO
        return BoardingPassDto.builder()
                .reservationId(reservationId)
                .boardingPassCode(code)
                .build();
    }

    // Sadə boarding pass kod generatoru (6 simvolluq, qarışıq hərf/rəqəm)
    private String generateBoardingPass(Long reservationId) {
        String alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // oxşar simvollar (O/0, I/1) çıxarılıb
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) sb.append(alphabet.charAt(RNG.nextInt(alphabet.length())));
        return "BP-" + reservationId + "-" + sb;
    }
}
