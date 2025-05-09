package com.example.flightreservationsystem.service;

import com.example.flightreservationsystem.entity.ReservationEntity;
import com.example.flightreservationsystem.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public List<ReservationEntity> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Optional<ReservationEntity> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    public ReservationEntity addReservation(ReservationEntity reservation) {
        return reservationRepository.save(reservation);
    }

    public ReservationEntity updateReservation(Long id, ReservationEntity reservation) {
        if (reservationRepository.existsById(id)) {
            reservation.setId(id);
            return reservationRepository.save(reservation);
        } else {
            return null;
        }
    }

    public void deleteReservation(Long id) {
        reservationRepository.deleteById(id);
    }

}
