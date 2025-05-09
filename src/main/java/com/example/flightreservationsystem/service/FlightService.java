package com.example.flightreservationsystem.service;

import com.example.flightreservationsystem.entity.FlightEntity;
import com.example.flightreservationsystem.repository.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FlightService {

    private final FlightRepository flightRepository;

    @Autowired
    public FlightService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    public List<FlightEntity> getAllFlights() {
        return flightRepository.findAll();
    }

    public Optional<FlightEntity> getFlightById(Long id) {
        return flightRepository.findById(id);
    }

    public FlightEntity addFlight(FlightEntity flight) {
        return flightRepository.save(flight);
    }

    public FlightEntity updateFlight(Long id, FlightEntity flight) {
        if (flightRepository.existsById(id)) {
            flight.setId(id);
            return flightRepository.save(flight);
        } else {
            return null;
        }
    }

    public void deleteFlight(Long id) {
        flightRepository.deleteById(id);
    }
}
