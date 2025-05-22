package com.example.flightreservationsystem.service;

import com.example.flightreservationsystem.Mapper.FlightMapper;
import com.example.flightreservationsystem.dto.request.FlightRequestDto;
import com.example.flightreservationsystem.dto.response.FlightResponseDto;
import com.example.flightreservationsystem.entity.FlightEntity;
import com.example.flightreservationsystem.repository.FlightRepository;
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
public class FlightService {

    private final FlightRepository flightRepository;
    private final FlightMapper flightMapper;

    public List<FlightResponseDto> getAllFlights() {
        log.info("START: butun ucuslar sorgulanir");
        List<FlightResponseDto> flights = flightRepository.findAll().stream()
                .map(flightMapper::toDto)
                .collect(Collectors.toList());
        log.info("END: {} ucus tapıldı", flights.size());
        return flights;
    }

    public Optional<FlightResponseDto> getFlightById(Long id) {
        log.info("START: ucus id ilə axtarılır: id={}", id);
        Optional<FlightResponseDto> flight = flightRepository.findById(id)
                .map(flightMapper::toDto);
        if (flight.isPresent()) {
            log.info("ucus tapıldı: id={}", id);
        } else {
            log.warn("ucus tapılmadı: id={}", id);
        }
        log.info("END: id ilə axtarılma tamamlandı");
        return flight;
    }

    public FlightResponseDto addFlight(FlightRequestDto flightRequestDto) {
        log.info("START: yeni ucus elave edilir: {}", flightRequestDto);
        FlightEntity flightEntity = flightMapper.toEntity(flightRequestDto);
        FlightEntity savedEntity = flightRepository.save(flightEntity);
        FlightResponseDto responseDto = flightMapper.toDto(savedEntity);
        log.info("ucus ugurla elave olundu: id={}", responseDto.getId());
        log.info("END: ucus elave edildi");
        return responseDto;
    }

    public FlightResponseDto updateFlight(Long id, FlightRequestDto flightRequestDto) {
        log.info("START: ucus yenilenir: id={}, yeni melumatlar: {}", id, flightRequestDto);
        if (flightRepository.existsById(id)) {
            FlightEntity flightEntity = flightMapper.toEntity(flightRequestDto);
            flightEntity.setId(id);
            FlightEntity updatedEntity = flightRepository.save(flightEntity);
            FlightResponseDto responseDto = flightMapper.toDto(updatedEntity);
            log.info("ucus ugurla yenilendi: id={}", id);
            log.info("END: ucus yenilendi ");
            return responseDto;
        } else {
            log.warn("yenilenme ugursuz oldu: ucus tapılmadı: id={}", id);
            log.info("END: ucus yenilenmesi tamamlandı - tapılmadı");
            return null;
        }
    }

    public void deleteFlight(Long id) {
        log.info("START: ucus silinir: id={}", id);
        flightRepository.deleteById(id);
        log.info("Uçuş ugurla silindi: id={}", id);
        log.info("END: silinme tamamlandı");
    }
}
