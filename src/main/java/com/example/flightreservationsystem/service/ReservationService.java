package com.example.flightreservationsystem.service;

import com.example.flightreservationsystem.dto.PaymentDto;
import com.example.flightreservationsystem.dto.ReservationDto;
import com.example.flightreservationsystem.entity.FlightEntity;
import com.example.flightreservationsystem.entity.PassengerEntity;
import com.example.flightreservationsystem.entity.ReservationEntity;
import com.example.flightreservationsystem.enums.ReservationStatus;
import com.example.flightreservationsystem.exception.ResourceNotFoundException;
import com.example.flightreservationsystem.mapper.FlightMapper;
import com.example.flightreservationsystem.mapper.PassengerMapper;
import com.example.flightreservationsystem.mapper.ReservationMapper;
import com.example.flightreservationsystem.repository.FlightRepository;
import com.example.flightreservationsystem.repository.PassengerRepository;
import com.example.flightreservationsystem.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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
    private final PaymentService paymentService;


    public List<ReservationDto> getAllReservations() {
        log.info("Fetching all reservations");
        return reservationRepository.findAll().stream()
                .map(reservationMapper::toDto)
                .toList();
    }

    public ReservationDto getReservationOrThrow(Long id) {
        log.info("Fetching reservation id={}", id);
        return reservationRepository.findById(id)
                .map(reservationMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with ID: " + id));
    }

    @Transactional
    public ReservationDto createReservation(ReservationDto dto) {
        log.info("Creating reservation (legacy), passengerId={}, flightId={}", dto.getPassengerId(), dto.getFlightId());

        PassengerEntity passenger = passengerRepository.findById(dto.getPassengerId())
                .orElseThrow(() -> new ResourceNotFoundException("Passenger not found with ID: " + dto.getPassengerId()));
        FlightEntity flight = flightRepository.findById(dto.getFlightId())
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found with ID: " + dto.getFlightId()));

        if (flight.getDepartureTime() != null && flight.getDepartureTime().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Cannot create reservation for past flights");
        }

        ReservationEntity entity = reservationMapper.toEntity(dto);
        entity.setPassenger(passenger);
        entity.setFlight(flight);
        entity.setReservationDate(LocalDateTime.now());
        if (entity.getStatus() == null) entity.setStatus(ReservationStatus.PENDING);

        ReservationEntity saved = reservationRepository.save(entity);
        return reservationMapper.toDto(saved);
    }

    @Transactional
    public ReservationDto createReservationWithPayment(ReservationDto reservationDto, PaymentDto paymentDto) {
        log.info("Creating reservation WITH payment, passengerId={}, flightId={}",
                reservationDto.getPassengerId(), reservationDto.getFlightId());

        PassengerEntity passenger = passengerRepository.findById(reservationDto.getPassengerId())
                .orElseThrow(() -> new ResourceNotFoundException("Passenger not found with ID: " + reservationDto.getPassengerId()));
        FlightEntity flight = flightRepository.findById(reservationDto.getFlightId())
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found with ID: " + reservationDto.getFlightId()));

        if (flight.getDepartureTime() != null && flight.getDepartureTime().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Cannot create reservation for past flights");
        }

        ReservationEntity entity = reservationMapper.toEntity(reservationDto);
        entity.setPassenger(passenger);
        entity.setFlight(flight);
        entity.setReservationDate(LocalDateTime.now());
        if (entity.getStatus() == null) entity.setStatus(ReservationStatus.PENDING);

        ReservationEntity saved = reservationRepository.save(entity);

        if (paymentDto == null) {
            throw new IllegalArgumentException("Payment information is required");
        }
        paymentDto.setReservationId(saved.getId());
        paymentService.processPayment(paymentDto);

        ReservationEntity refreshed = reservationRepository.findById(saved.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found after payment: " + saved.getId()));

        return reservationMapper.toDto(refreshed);
    }


    @Transactional
    public ReservationDto updateReservationOrThrow(Long id, ReservationDto dto) {
        log.info("Updating reservation id={}", id);

        ReservationEntity existing = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with ID: " + id));

        PassengerEntity passenger = passengerRepository.findById(dto.getPassengerId())
                .orElseThrow(() -> new ResourceNotFoundException("Passenger not found with ID: " + dto.getPassengerId()));
        FlightEntity flight = flightRepository.findById(dto.getFlightId())
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found with ID: " + dto.getFlightId()));

        if (flight.getDepartureTime() != null && flight.getDepartureTime().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Cannot move reservation to a past flight");
        }

        ReservationEntity updated = reservationMapper.toEntity(dto);
        updated.setId(existing.getId());
        updated.setPassenger(passenger);
        updated.setFlight(flight);
        updated.setReservationDate(existing.getReservationDate());

        return reservationMapper.toDto(reservationRepository.save(updated));
    }

    @Transactional
    public void deleteReservation(Long id) {
        log.info("Deleting reservation id={}", id);
        if (!reservationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Reservation not found with ID: " + id);
        }
        reservationRepository.deleteById(id);
        log.info("Reservation deleted id={}", id);
    }

    @Transactional
    public ReservationDto updateReservationStatus(Long id, ReservationStatus status) {
        ReservationEntity reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with ID: " + id));
        reservation.setStatus(status);
        return reservationMapper.toDto(reservationRepository.save(reservation));
    }

    @Transactional
    public ReservationDto cancelReservation(Long id) {
        ReservationEntity reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with ID: " + id));

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            return reservationMapper.toDto(reservation);
        }

        if (reservation.getStatus() == ReservationStatus.CONFIRMED ||
                reservation.getStatus() == ReservationStatus.PENDING) {

            reservation.setStatus(ReservationStatus.CANCELLED);
            ReservationEntity saved = reservationRepository.save(reservation);

            try {
                paymentService.refundPayment(id);
            } catch (Exception e) {
                log.warn("Refund failed for reservation {}: {}", id, e.getMessage());
            }

            return reservationMapper.toDto(saved);
        }

        throw new IllegalStateException("Cannot cancel reservation in status: " + reservation.getStatus());
    }

    public List<ReservationDto> getReservationHistory() {
        log.info("Retrieving cancelled reservation history");
        return reservationRepository.findAll().stream()
                .filter(r -> r.getStatus() == ReservationStatus.CANCELLED)
                .map(reservationMapper::toDto)
                .toList();
    }


    public void sendUpcomingFlightReminders() {
        log.info("Running daily reminder job for upcoming confirmed flights...");
        List<ReservationEntity> reservations = reservationRepository.findTomorrowConfirmedReservations();
        log.info("Found {} confirmed reservations for tomorrow", reservations.size());

        for (ReservationEntity reservation : reservations) {
            try {
                mailService.sendReminderEmail(
                        passengerMapper.toDto(reservation.getPassenger()),
                        flightMapper.toDto(reservation.getFlight())
                );
                log.info("Reminder email sent to {}", reservation.getPassenger().getEmail());
            } catch (Exception e) {
                log.warn("Failed to send reminder email: {}", e.getMessage());
            }
        }
    }
}
