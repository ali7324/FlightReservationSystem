package com.example.flightreservationsystem.service;

import com.example.flightreservationsystem.mapper.ReservationMapper;
import com.example.flightreservationsystem.dto.ReservationDto;
import com.example.flightreservationsystem.entity.ReservationEntity;
import com.example.flightreservationsystem.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;
    private final MailService mailService;

    public List<ReservationDto> getAllReservations() {
        log.info("Fetching all reservations");
        return reservationRepository.findAll().stream()
                .map(reservationMapper::toDto)
                .collect(Collectors.toList());
    }

    public ReservationDto getReservationOrThrow(Long id) {
        log.info("Fetching reservation with ID: {}", id);
        return reservationRepository.findById(id)
                .map(reservationMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with ID: " + id));
    }

    public ReservationDto createReservation(ReservationDto dto) {
        log.info("Creating reservation for passenger ID: {}", dto.getPassengerId());

        ReservationEntity entity = reservationMapper.toEntity(dto);
        ReservationEntity saved = reservationRepository.save(entity);

        try {
            mailService.sendReservationConfirmationMail(dto.getPassenger(), dto.getFlight());
            log.info("Confirmation email sent to: {}", dto.getPassenger().getGmail());
        } catch (Exception e) {
            log.error("Failed to send confirmation email: {}", e.getMessage());
        }

        log.info("Reservation created with ID: {}", saved.getId());
        return reservationMapper.toDto(saved);
    }

    public ReservationDto updateReservationOrThrow(Long id, ReservationDto dto) {
        log.info("Updating reservation with ID: {}", id);

        ReservationEntity existing = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with ID: " + id));

        ReservationEntity updated = reservationMapper.toEntity(dto);
        updated.setId(existing.getId());

        ReservationEntity saved = reservationRepository.save(updated);
        log.info("Reservation updated with ID: {}", saved.getId());
        return reservationMapper.toDto(saved);
    }

    public void deleteReservation(Long id) {
        log.info("Deleting reservation with ID: {}", id);
        if (!reservationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Reservation not found with ID: " + id);
        }
        reservationRepository.deleteById(id);
        log.info("Reservation deleted with ID: {}", id);
    }
}
