package com.example.flightreservationsystem.service;

import com.example.flightreservationsystem.dto.FlightDto;
import com.example.flightreservationsystem.entity.FlightEntity;
import com.example.flightreservationsystem.exception.ResourceNotFoundException;
import com.example.flightreservationsystem.mapper.FlightMapper;
import com.example.flightreservationsystem.repository.FlightRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightServiceTest {

    @Mock private FlightRepository flightRepository;
    @Mock private FlightMapper flightMapper;

    @InjectMocks
    private FlightService flightService;


    private FlightEntity entity(Long id, String no, LocalDateTime dep, LocalDateTime arr) {
        FlightEntity e = new FlightEntity();
        e.setId(id);
        e.setFlightNumber(no);
        e.setDeparture("BAK");
        e.setDestination("IST");
        e.setDepartureTime(dep);
        e.setArrivalTime(arr);
        e.setPrice(new BigDecimal("199.99"));
        return e;
    }

    private FlightDto dto(Long id, String no, LocalDateTime dep, LocalDateTime arr) {
        return FlightDto.builder()
                .id(id)
                .flightNumber(no)
                .departure("BAK")
                .destination("IST")
                .departureTime(dep)
                .arrivalTime(arr)
                .price(new BigDecimal("199.99"))
                .build();
    }


    @Test
    @DisplayName("getAllFlights(): repo -> mapper -> dto list")
    void getAllFlights_ok() {
        LocalDateTime dep = LocalDateTime.now().plusDays(1);
        LocalDateTime arr = dep.plusHours(2);
        var e1 = entity(1L, "TG100", dep, arr);
        var e2 = entity(2L, "TG200", dep.plusDays(1), arr.plusDays(1));

        when(flightRepository.findAll()).thenReturn(List.of(e1, e2));
        when(flightMapper.toDto(e1)).thenReturn(dto(1L, "TG100", dep, arr));
        when(flightMapper.toDto(e2)).thenReturn(dto(2L, "TG200", dep.plusDays(1), arr.plusDays(1)));

        List<FlightDto> res = flightService.getAllFlights();

        assertEquals(2, res.size());
        assertEquals("TG100", res.get(0).getFlightNumber());
        assertEquals("TG200", res.get(1).getFlightNumber());

        verify(flightRepository).findAll();
        verify(flightMapper).toDto(e1);
        verify(flightMapper).toDto(e2);
    }

    @Test
    @DisplayName("getFlightOrThrow(): tapılırsa dto qaytarır; tapılmadıqda atır")
    void getFlight_found_and_notFound() {
        Long id = 5L;
        LocalDateTime dep = LocalDateTime.now().plusDays(2);
        LocalDateTime arr = dep.plusHours(3);
        var e = entity(id, "TG500", dep, arr);
        var d = dto(id, "TG500", dep, arr);

        when(flightRepository.findById(id)).thenReturn(Optional.of(e));
        when(flightMapper.toDto(e)).thenReturn(d);

        FlightDto res = flightService.getFlightOrThrow(id);

        assertEquals(id, res.getId());
        assertEquals("TG500", res.getFlightNumber());

        when(flightRepository.findById(404L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> flightService.getFlightOrThrow(404L));
    }

    @Nested
    @DisplayName("createFlight()")
    class CreateFlightTests {

        @Test
        @DisplayName("Uğurlu: mapper/save çağırılır, dto qaytarılır")
        void create_ok() {
            LocalDateTime dep = LocalDateTime.now().plusDays(1);
            LocalDateTime arr = dep.plusHours(2);

            FlightDto in = dto(null, "TG101", dep, arr);
            FlightEntity mapped = entity(null, "TG101", dep, arr);
            FlightEntity saved = entity(10L, "TG101", dep, arr);
            FlightDto out = dto(10L, "TG101", dep, arr);

            when(flightMapper.toEntity(in)).thenReturn(mapped);
            when(flightRepository.save(mapped)).thenReturn(saved);
            when(flightMapper.toDto(saved)).thenReturn(out);

            FlightDto res = flightService.createFlight(in);

            assertEquals(10L, res.getId());
            assertEquals("TG101", res.getFlightNumber());
            verify(flightMapper).toEntity(in);
            verify(flightRepository).save(mapped);
            verify(flightMapper).toDto(saved);
        }
    }

    @Nested
    @DisplayName("updateFlightOrThrow()")
    class UpdateFlightTests {

        @Test
        @DisplayName("Uğurlu: existing tapılır, ID qorunur, save + toDto çağırılır")
        void update_ok_setsIdAndSaves() {
            Long id = 20L;
            LocalDateTime dep = LocalDateTime.now().plusDays(1);
            LocalDateTime arr = dep.plusHours(3);

            FlightEntity existing = entity(id, "OLD", dep.plusDays(1), arr.plusDays(1));
            FlightDto in = dto(null, "NEW", dep, arr);
            FlightEntity mapped = entity(null, "NEW", dep, arr);
            FlightEntity saved = entity(id, "NEW", dep, arr);

            when(flightRepository.findById(id)).thenReturn(Optional.of(existing));
            when(flightMapper.toEntity(in)).thenReturn(mapped);
            when(flightRepository.save(any(FlightEntity.class))).thenReturn(saved);
            when(flightMapper.toDto(saved)).thenReturn(dto(id, "NEW", dep, arr));

            FlightDto res = flightService.updateFlightOrThrow(id, in);

            assertEquals(id, res.getId());
            assertEquals("NEW", res.getFlightNumber());

            ArgumentCaptor<FlightEntity> captor = ArgumentCaptor.forClass(FlightEntity.class);
            verify(flightRepository).save(captor.capture());
            assertEquals(id, captor.getValue().getId());
            assertEquals("NEW", captor.getValue().getFlightNumber());

            verify(flightMapper).toEntity(in);
            verify(flightMapper).toDto(saved);
        }

        @Test
        @DisplayName("Tapılmadıqda: ResourceNotFoundException")
        void update_notFound() {
            when(flightRepository.findById(99L)).thenReturn(Optional.empty());
            FlightDto in = dto(null, "X", LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

            assertThrows(ResourceNotFoundException.class,
                    () -> flightService.updateFlightOrThrow(99L, in));
        }
    }

    @Nested
    @DisplayName("deleteFlight()")
    class DeleteFlightTests {

        @Test
        @DisplayName("Var olanda silinir")
        void delete_exists() {
            when(flightRepository.existsById(7L)).thenReturn(true);

            flightService.deleteFlight(7L);

            verify(flightRepository).deleteById(7L);
        }

        @Test
        @DisplayName("Yoxdursa: ResourceNotFoundException")
        void delete_notExists() {
            when(flightRepository.existsById(8L)).thenReturn(false);

            assertThrows(ResourceNotFoundException.class,
                    () -> flightService.deleteFlight(8L));
            verify(flightRepository, never()).deleteById(anyLong());
        }
    }

    @Test
    @DisplayName("searchFlights(): repo nəticələri mapper ilə DTO-ya çevrilir")
    void searchFlights_ok() {
        LocalDate d = LocalDate.now().plusDays(3);
        var e1 = entity(1L, "TG10", LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(3).plusHours(1));
        var e2 = entity(2L, "TG20", LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(3).plusHours(2));
        var d1 = dto(1L, "TG10", e1.getDepartureTime(), e1.getArrivalTime());
        var d2 = dto(2L, "TG20", e2.getDepartureTime(), e2.getArrivalTime());

        when(flightRepository.searchFlights("TG", "BAK", "GNY", d)).thenReturn(List.of(e1, e2));
        when(flightMapper.toDto(e1)).thenReturn(d1);
        when(flightMapper.toDto(e2)).thenReturn(d2);

        List<FlightDto> res = flightService.searchFlights("TG", "BAK", "GNY", d);

        assertEquals(List.of(d1, d2), res);
        verify(flightRepository).searchFlights("TG", "BAK", "GNY", d);
        verify(flightMapper).toDto(e1);
        verify(flightMapper).toDto(e2);
    }
}
