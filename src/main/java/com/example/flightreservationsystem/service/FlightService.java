package com.example.flightreservationsystem.service;

import com.example.flightreservationsystem.exception.ResourceNotFoundException;
import com.example.flightreservationsystem.mapper.FlightMapper;
import com.example.flightreservationsystem.dto.FlightDto;
import com.example.flightreservationsystem.entity.FlightEntity;
import com.example.flightreservationsystem.repository.FlightRepository;
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
public class FlightService {

    private final FlightRepository flightRepository;
    private final FlightMapper flightMapper;

    public List<FlightDto> getAllFlights() {
        log.info("Fetching all flights");
        return flightRepository.findAll().stream()
                .map(flightMapper::toDto)
                .collect(Collectors.toList());
    }

    public FlightDto getFlightOrThrow(Long id) {
        log.info("Fetching flight with ID: {}", id);
        return flightRepository.findById(id)
                .map(flightMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found with ID: " + id));
    }

    public FlightDto createFlight(FlightDto flightDto) {
        log.info("Creating new flight: {}", flightDto.getFlightNumber());
        FlightEntity entity = flightMapper.toEntity(flightDto);
        FlightEntity saved = flightRepository.save(entity);
        log.info("Flight created with ID: {}", saved.getId());
        return flightMapper.toDto(saved);
    }

    public FlightDto updateFlightOrThrow(Long id, FlightDto flightDto) {
        log.info("Updating flight with ID: {}", id);
        FlightEntity existing = flightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found with ID: " + id));

        FlightEntity updated = flightMapper.toEntity(flightDto);
        updated.setId(existing.getId());

        FlightEntity saved = flightRepository.save(updated);
        log.info("Flight updated successfully with ID: {}", saved.getId());
        return flightMapper.toDto(saved);
    }

    public void deleteFlight(Long id) {
        log.info("Deleting flight with ID: {}", id);
        if (!flightRepository.existsById(id)) {
            throw new ResourceNotFoundException("Flight not found with ID: " + id);
        }
        flightRepository.deleteById(id);
        log.info("Flight deleted with ID: {}", id);
    }
}
