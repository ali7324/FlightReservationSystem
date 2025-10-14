package com.example.flightreservationsystem.service;

import com.example.flightreservationsystem.dto.FlightDto;
import com.example.flightreservationsystem.entity.FlightEntity;
import com.example.flightreservationsystem.exception.ResourceNotFoundException;
import com.example.flightreservationsystem.mapper.FlightMapper;
import com.example.flightreservationsystem.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlightService {

    private final FlightRepository flightRepository;
    private final FlightMapper flightMapper;

    public List<FlightDto> getAllFlights() {
        log.info("Fetching all flights");
        return flightRepository.findAll().stream()
                .map(flightMapper::toDto)
                .toList();
    }

    public FlightDto getFlightOrThrow(Long id) {
        log.info("Fetching flight with ID: {}", id);
        return flightRepository.findById(id)
                .map(flightMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found with ID: " + id));
    }

    @Transactional
    public FlightDto createFlight(FlightDto flightDto) {
        validateFlightTimes(flightDto.getDepartureTime(), flightDto.getArrivalTime());
        log.info("Creating flight {}", flightDto.getFlightNumber());
        FlightEntity saved = flightRepository.save(flightMapper.toEntity(flightDto));
        return flightMapper.toDto(saved);
    }

    @Transactional
    public FlightDto updateFlightOrThrow(Long id, FlightDto flightDto) {
        log.info("Updating flight {}", id);
        FlightEntity existing = flightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found with ID: " + id));

        validateFlightTimes(flightDto.getDepartureTime(), flightDto.getArrivalTime());

        FlightEntity updated = flightMapper.toEntity(flightDto);
        updated.setId(existing.getId());
        return flightMapper.toDto(flightRepository.save(updated));
    }

    @Transactional
    public void deleteFlight(Long id) {
        log.info("Deleting flight {}", id);
        if (!flightRepository.existsById(id)) {
            throw new ResourceNotFoundException("Flight not found with ID: " + id);
        }
        flightRepository.deleteById(id);
    }

    public List<FlightEntity> searchRaw(String flightNumber, String departure, String destination, LocalDate departureDate) {
        return flightRepository.searchFlights(flightNumber, departure, destination, departureDate);
    }

    public List<FlightDto> searchFlights(String flightNumber, String departure, String destination, LocalDate departureDate) {
        return searchRaw(flightNumber, departure, destination, departureDate).stream()
                .map(flightMapper::toDto)
                .toList();
    }

    private void validateFlightTimes(LocalDateTime dep, LocalDateTime arr) {
        if (dep == null || arr == null) throw new IllegalArgumentException("Departure and arrival times are required");
        if (dep.isBefore(LocalDateTime.now())) throw new IllegalArgumentException("Departure time must be in the future");
        if (!arr.isAfter(dep)) throw new IllegalArgumentException("Arrival time must be after departure time");
    }
}
