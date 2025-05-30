package com.example.flightreservationsystem.service;

import com.example.flightreservationsystem.dto.FlightDto;
import com.example.flightreservationsystem.dto.PassengerDto;
import com.example.flightreservationsystem.entity.FlightEntity;
import com.example.flightreservationsystem.entity.PassengerEntity;
import com.example.flightreservationsystem.enums.ReservationStatus;
import com.example.flightreservationsystem.exception.ResourceNotFoundException;
import com.example.flightreservationsystem.mapper.FlightMapper;
import com.example.flightreservationsystem.mapper.PassengerMapper;
import com.example.flightreservationsystem.mapper.ReservationMapper;
import com.example.flightreservationsystem.dto.ReservationDto;
import com.example.flightreservationsystem.entity.ReservationEntity;
import com.example.flightreservationsystem.repository.FlightRepository;
import com.example.flightreservationsystem.repository.PassengerRepository;
import com.example.flightreservationsystem.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {


    private final ReservationRepository reservationRepository;
    private final FlightRepository flightRepository;
    private final PassengerRepository passengerRepository;
    private final ReservationMapper reservationMapper;
    private final PassengerMapper passengerMapper;
    private final FlightMapper flightMapper;
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

        PassengerEntity passenger = passengerRepository.findById(dto.getPassengerId())
                .orElseThrow(() -> new ResourceNotFoundException("Passenger not found with ID: " + dto.getPassengerId()));

        FlightEntity flight = flightRepository.findById(dto.getFlightId())
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found with ID: " + dto.getFlightId()));

        ReservationEntity entity = reservationMapper.toEntity(dto);
        entity.setPassenger(passenger);
        entity.setFlight(flight);

        ReservationEntity saved = reservationRepository.save(entity);

        try {
            mailService.sendReservationConfirmationMail(passengerMapper.toDto(passenger), flightMapper.toDto(flight));
            log.info("Confirmation email sent to: {}", passenger.getGmail());
        } catch (Exception e) {
            log.error("Failed to send confirmation email: {}", e.getMessage());
        }

        return reservationMapper.toDto(saved);
    }

    public ReservationDto updateReservationOrThrow(Long id, ReservationDto dto) {
        log.info("Updating reservation with ID: {}", id);

        ReservationEntity existing = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with ID: " + id));

        PassengerEntity passenger = passengerRepository.findById(dto.getPassengerId())
                .orElseThrow(() -> new ResourceNotFoundException("Passenger not found with ID: " + dto.getPassengerId()));

        FlightEntity flight = flightRepository.findById(dto.getFlightId())
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found with ID: " + dto.getFlightId()));

        ReservationEntity updated = reservationMapper.toEntity(dto);
        updated.setId(existing.getId());
        updated.setPassenger(passenger);
        updated.setFlight(flight);

        return reservationMapper.toDto(reservationRepository.save(updated));
    }

    public void deleteReservation(Long id) {
        log.info("Deleting reservation with ID: {}", id);
        if (!reservationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Reservation not found with ID: " + id);
        }
        reservationRepository.deleteById(id);
        log.info("Reservation deleted with ID: {}", id);
    }

    public ReservationDto updateReservationStatus(Long id, ReservationStatus status) {
        log.info("Updating reservation status. ID: {}, New Status: {}", id, status);

        ReservationEntity reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with ID: " + id));

        reservation.setStatus(status);
        return reservationMapper.toDto(reservationRepository.save(reservation));
    }

    public ReservationDto cancelReservation(Long id) {
        log.info("Cancelling reservation with ID: {}", id);

        ReservationEntity reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with ID: " + id));

        reservation.setStatus(ReservationStatus.CANCELLED);
        return reservationMapper.toDto(reservationRepository.save(reservation));
    }

    public List<ReservationDto> getReservationHistory() {
        log.info("Retrieving cancelled reservation history");

        return reservationRepository.findAll().stream()
                .filter(r -> r.getStatus() == ReservationStatus.CANCELLED)
                .map(reservationMapper::toDto)
                .collect(Collectors.toList());
    }

    public void sendUpcomingFlightReminders() {
        log.info("Running daily reminder job for upcoming confirmed flights");

        List<ReservationEntity> reservations = reservationRepository.findTomorrowConfirmedReservations();
        log.info("Found {} confirmed reservations for tomorrow", reservations.size());

        for (ReservationEntity reservation : reservations) {
            try {
                mailService.sendReminderEmail(
                        passengerMapper.toDto(reservation.getPassenger()),
                        flightMapper.toDto(reservation.getFlight())
                );
                log.info("Reminder email sent to {}", reservation.getPassenger().getGmail());
            } catch (Exception e) {
                log.error("Failed to send reminder email: {}", e.getMessage());
            }
        }
    }
}
