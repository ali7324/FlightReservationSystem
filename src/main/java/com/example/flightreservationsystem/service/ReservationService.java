package com.example.flightreservationsystem.service;

import com.example.flightreservationsystem.Mapper.ReservationMapper;
import com.example.flightreservationsystem.dto.request.ReservationRequestDto;
import com.example.flightreservationsystem.dto.response.ReservationResponseDto;
import com.example.flightreservationsystem.entity.ReservationEntity;
import com.example.flightreservationsystem.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;
    private final MailService mailService;


    public List<ReservationResponseDto> getAllReservations() {
        log.info("START: butun rezervasiyalar sorgulanir");
        List<ReservationResponseDto> reservations = reservationRepository.findAll().stream()
                .map(reservationMapper::toDto)
                .collect(Collectors.toList());
        log.info("END: {} rezervasiya tapıldı", reservations.size());
        return reservations;
    }

    public Optional<ReservationResponseDto> getReservationById(Long id) {
        log.info("START: rezervasiya id ilə axtarılır: id={}", id);
        Optional<ReservationResponseDto> reservation = reservationRepository.findById(id)
                .map(reservationMapper::toDto);
        if (reservation.isPresent()) {
            log.info("rezervasiya tapıldı: id={}", id);
        } else {
            log.warn("rezervasiya tapılmadı: id={}", id);
        }
        log.info("END: rezervasiya id ilə axtarılma tamamlandı");
        return reservation;
    }


    public ReservationResponseDto updateReservation(Long id, ReservationRequestDto dto) {
        log.info("START: rezervasiya yenilenir: id={}, yeni melumatlar: {}", id, dto);
        if (reservationRepository.existsById(id)) {
            ReservationEntity entity = reservationMapper.toEntity(dto);
            entity.setId(id);
            ReservationEntity updated = reservationRepository.save(entity);
            ReservationResponseDto response = reservationMapper.toDto(updated);
            log.info("rezervasiya uğurla yeniləndi: id={}", id);
            log.info("END: rezervasiya yenilenmesi tamamlandı");
            return response;
        } else {
            log.warn("yenilenme ugursuz oldu: rezervasiya tapılmadı: id={}", id);
            log.info("END: rezervasiya yenilenmesi tamamlandı - tapılmadı");
            return null;
        }
    }

    public void deleteReservation(Long id) {
        log.info("START: rezervasiya silinir: id={}", id);
        reservationRepository.deleteById(id);
        log.info("rezervasiya ugurla silindi: id={}", id);
        log.info("END: rezervasiya silindi tamamlandı");
    }

    public ReservationResponseDto addReservation(ReservationRequestDto dto) {
        log.info("START: yeni rezervasiya elave edilir: {}", dto);
        ReservationEntity entity = reservationMapper.toEntity(dto);
        ReservationEntity saved = reservationRepository.save(entity);
        ReservationResponseDto response = reservationMapper.toDto(saved);

        mailService.sendReservationConfirmationMail(saved.getPassenger(), saved.getFlight());

        log.info("rezervasiya ugurla elave olundu: id={}", response.getId());
        log.info("END: rezervasiya elave edilmesi tamamlandı");
        return response;
    }

}
