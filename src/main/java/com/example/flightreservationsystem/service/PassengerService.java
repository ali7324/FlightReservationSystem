package com.example.flightreservationsystem.service;

import com.example.flightreservationsystem.exception.ResourceNotFoundException;
import com.example.flightreservationsystem.mapper.PassengerMapper;
import com.example.flightreservationsystem.dto.PassengerDto;
import com.example.flightreservationsystem.entity.PassengerEntity;
import com.example.flightreservationsystem.repository.PassengerRepository;
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
public class PassengerService {

    private final PassengerRepository passengerRepository;
    private final PassengerMapper passengerMapper;

    public List<PassengerDto> getAllPassengers() {
        log.info("Fetching all passengers");
        return passengerRepository.findAll().stream()
                .map(passengerMapper::toDto)
                .collect(Collectors.toList());
    }

    public PassengerDto getPassengerOrThrow(Long id) {
        log.info("Fetching passenger with ID: {}", id);
        return passengerRepository.findById(id)
                .map(passengerMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Passenger not found with ID: " + id));
    }

    public PassengerDto createPassenger(PassengerDto dto) {
        log.info("Creating passenger: {} {}", dto.getFirstName(), dto.getLastName());
        PassengerEntity entity = passengerMapper.toEntity(dto);
        PassengerEntity saved = passengerRepository.save(entity);
        log.info("Passenger created with ID: {}", saved.getId());
        return passengerMapper.toDto(saved);
    }

    public PassengerDto updatePassengerOrThrow(Long id, PassengerDto dto) {
        log.info("Updating passenger with ID: {}", id);
        PassengerEntity existing = passengerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Passenger not found with ID: " + id));

        PassengerEntity updated = passengerMapper.toEntity(dto);
        updated.setId(existing.getId());

        PassengerEntity saved = passengerRepository.save(updated);
        log.info("Passenger updated with ID: {}", saved.getId());
        return passengerMapper.toDto(saved);
    }

    public void deletePassenger(Long id) {
        log.info("Deleting passenger with ID: {}", id);
        if (!passengerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Passenger not found with ID: " + id);
        }
        passengerRepository.deleteById(id);
        log.info("Passenger deleted with ID: {}", id);
    }
}
