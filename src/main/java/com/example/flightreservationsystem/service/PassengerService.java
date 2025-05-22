package com.example.flightreservationsystem.service;

import com.example.flightreservationsystem.Mapper.PassengerMapper;
import com.example.flightreservationsystem.dto.request.PassengerRequestDto;
import com.example.flightreservationsystem.dto.response.PassengerResponseDto;
import com.example.flightreservationsystem.entity.PassengerEntity;
import com.example.flightreservationsystem.repository.PassengerRepository;
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
public class PassengerService {
    private final PassengerRepository passengerRepository;
    private final PassengerMapper passengerMapper;

    public List<PassengerResponseDto> getAllPassengers() {
        log.info("START: butun sernisinler sorgulanir");
        List<PassengerResponseDto> passengers = passengerRepository.findAll().stream()
                .map(passengerMapper::toDto)
                .collect(Collectors.toList());
        log.info("END: {} sernisin tapıldı", passengers.size());
        return passengers;
    }

    public Optional<PassengerResponseDto> getPassengerById(Long id) {
        log.info("START: sernisin id ilə axtarılır: id={}", id);
        Optional<PassengerResponseDto> passenger = passengerRepository.findById(id)
                .map(passengerMapper::toDto);
        if (passenger.isPresent()) {
            log.info("sernisin tapıldı: id={}", id);
        } else {
            log.warn("sernisin tapılmadı: id={}", id);
        }
        log.info("END: id ile axtarilma tamamlandı");
        return passenger;
    }

    public PassengerResponseDto addPassenger(PassengerRequestDto dto) {
        log.info("START: yeni sernisin elave edilir: {}", dto);
        PassengerEntity entity = passengerMapper.toEntity(dto);
        PassengerEntity saved = passengerRepository.save(entity);
        PassengerResponseDto response = passengerMapper.toDto(saved);
        log.info("sernisin ugurla elave olundu: id={}", response.getId());
        log.info("END: sernisin elavesi tamamlandı");
        return response;
    }

    public PassengerResponseDto updatePassenger(Long id, PassengerRequestDto dto) {
        log.info("START: sernisin yenilenir: id={}, yeni melumatlar: {}", id, dto);
        if (passengerRepository.existsById(id)) {
            PassengerEntity entity = passengerMapper.toEntity(dto);
            entity.setId(id);
            PassengerEntity updated = passengerRepository.save(entity);
            PassengerResponseDto response = passengerMapper.toDto(updated);
            log.info("sernisin ugurla yenilendi: id={}", id);
            log.info("END: yenilenme tamamlandı");
            return response;
        } else {
            log.warn("yenilenme ugursuz oldu: sernisin tapılmadı: id={}", id);
            log.info("END: yenilenme tamamlandı - tapılmadı");
            return null;
        }
    }

    public void deletePassenger(Long id) {
        log.info("START: sernisin silinir: id={}", id);
        passengerRepository.deleteById(id);
        log.info("sernisin ugurla silindi: id={}", id);
        log.info("END: silinme tamamlandı");
    }
}
