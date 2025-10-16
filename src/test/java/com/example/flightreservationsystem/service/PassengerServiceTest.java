package com.example.flightreservationsystem.service;

import com.example.flightreservationsystem.dto.PassengerDto;
import com.example.flightreservationsystem.entity.PassengerEntity;
import com.example.flightreservationsystem.exception.ResourceNotFoundException;
import com.example.flightreservationsystem.mapper.PassengerMapper;
import com.example.flightreservationsystem.repository.PassengerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PassengerServiceTest {

    @Mock private PassengerRepository passengerRepository;
    @Mock private PassengerMapper passengerMapper;

    @InjectMocks
    private PassengerService passengerService;


    private PassengerEntity entity(Long id, String first, String last, String email) {
        PassengerEntity e = new PassengerEntity();
        e.setId(id);
        e.setFirstName(first);
        e.setLastName(last);
        e.setAge(25);
        e.setGender("Male");
        LocalDate dob = LocalDate.now().minusYears(25);
        e.setDateOfBirth(dob);
        e.setEmail(email);
        return e;
    }

    private PassengerDto dto(Long id, String first, String last, String email) {
        LocalDate dob = LocalDate.now().minusYears(25);
        return PassengerDto.builder()
                .id(id)
                .firstName(first)
                .lastName(last)
                .age(25)
                .gender("Male")
                .dateOfBirth(dob)
                .email(email)
                .build();
    }

    @Test
    @DisplayName("getAllPassengers: repo -> mapper -> dto list")
    void getAllPassengers_ok() {
        PassengerEntity e1 = entity(1L, "Ali", "Qafarov", "a@ex.com");
        PassengerEntity e2 = entity(2L, "Leyla", "M.", "l@ex.com");
        PassengerDto d1 = dto(1L, "Ali", "Qafarov", "a@ex.com");
        PassengerDto d2 = dto(2L, "Leyla", "M.", "l@ex.com");

        when(passengerRepository.findAll()).thenReturn(List.of(e1, e2));
        when(passengerMapper.toDto(e1)).thenReturn(d1);
        when(passengerMapper.toDto(e2)).thenReturn(d2);

        List<PassengerDto> res = passengerService.getAllPassengers();

        assertEquals(2, res.size());
        assertEquals(d1, res.get(0));
        assertEquals(d2, res.get(1));

        verify(passengerRepository).findAll();
        verify(passengerMapper).toDto(e1);
        verify(passengerMapper).toDto(e2);
    }

    @Test
    @DisplayName("getPassengerOrThrow: tapıldısa DTO qaytarır")
    void getPassenger_found() {
        Long id = 5L;
        PassengerEntity e = entity(id, "Orxan", "H.", "o@ex.com");
        PassengerDto d = dto(id, "Orxan", "H.", "o@ex.com");

        when(passengerRepository.findById(id)).thenReturn(Optional.of(e));
        when(passengerMapper.toDto(e)).thenReturn(d);

        PassengerDto res = passengerService.getPassengerOrThrow(id);

        assertEquals(d, res);
        verify(passengerRepository).findById(id);
        verify(passengerMapper).toDto(e);
    }

    @Test
    @DisplayName("getPassengerOrThrow: tapılmadıqda ResourceNotFoundException")
    void getPassenger_notFound() {
        when(passengerRepository.findById(404L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> passengerService.getPassengerOrThrow(404L));
    }

    @Nested
    @DisplayName("createPassenger()")
    class CreatePassengerTests {

        @Test
        @DisplayName("Uğurlu: mapper->entity, save, mapper->dto")
        void create_ok() {
            PassengerDto in = dto(null, "Nigar", "B.", "n@ex.com");
            PassengerEntity mapped = entity(null, "Nigar", "B.", "n@ex.com");
            PassengerEntity saved = entity(10L, "Nigar", "B.", "n@ex.com");
            PassengerDto out = dto(10L, "Nigar", "B.", "n@ex.com");

            when(passengerMapper.toEntity(in)).thenReturn(mapped);
            when(passengerRepository.save(mapped)).thenReturn(saved);
            when(passengerMapper.toDto(saved)).thenReturn(out);

            PassengerDto res = passengerService.createPassenger(in);

            assertEquals(out, res);
            verify(passengerMapper).toEntity(in);
            verify(passengerRepository).save(mapped);
            verify(passengerMapper).toDto(saved);
        }
    }

    @Nested
    @DisplayName("updatePassengerOrThrow()")
    class UpdatePassengerTests {

        @Test
        @DisplayName("Tapılmadıqda: ResourceNotFoundException")
        void update_notFound() {
            when(passengerRepository.findById(99L)).thenReturn(Optional.empty());
            PassengerDto in = dto(null, "X", "Y", "x@ex.com");

            assertThrows(ResourceNotFoundException.class,
                    () -> passengerService.updatePassengerOrThrow(99L, in));
        }

        @Test
        @DisplayName("Uğurlu: existing tapılır, mapper-dən gələn entity-yə existing.id qoyulur və save olunur")
        void update_ok_setsIdAndSaves() {
            Long id = 20L;
            PassengerEntity existing = entity(id, "Old", "Name", "old@ex.com");
            PassengerDto in = dto(null, "New", "Name", "new@ex.com");
            PassengerEntity mapped = entity(null, "New", "Name", "new@ex.com");
            PassengerEntity saved = entity(id, "New", "Name", "new@ex.com");
            PassengerDto out = dto(id, "New", "Name", "new@ex.com");

            when(passengerRepository.findById(id)).thenReturn(Optional.of(existing));
            when(passengerMapper.toEntity(in)).thenReturn(mapped);
            when(passengerRepository.save(any(PassengerEntity.class))).thenReturn(saved);
            when(passengerMapper.toDto(saved)).thenReturn(out);

            PassengerDto res = passengerService.updatePassengerOrThrow(id, in);
            assertEquals(out, res);

            ArgumentCaptor<PassengerEntity> captor = ArgumentCaptor.forClass(PassengerEntity.class);
            verify(passengerRepository).save(captor.capture());
            assertEquals(id, captor.getValue().getId());
            assertEquals("New", captor.getValue().getFirstName());

            verify(passengerMapper).toEntity(in);
            verify(passengerMapper).toDto(saved);
        }
    }

    @Nested
    @DisplayName("deletePassenger()")
    class DeletePassengerTests {

        @Test
        @DisplayName("Var olanda: deleteById çağırılır")
        void delete_exists() {
            when(passengerRepository.existsById(7L)).thenReturn(true);

            passengerService.deletePassenger(7L);

            verify(passengerRepository).deleteById(7L);
        }

        @Test
        @DisplayName("Yoxdursa: ResourceNotFoundException və delete çağırılmır")
        void delete_notExists() {
            when(passengerRepository.existsById(8L)).thenReturn(false);

            assertThrows(ResourceNotFoundException.class,
                    () -> passengerService.deletePassenger(8L));
            verify(passengerRepository, never()).deleteById(anyLong());
        }
    }
}
