package com.example.flightreservationsystem.service;

import com.example.flightreservationsystem.dto.FlightDto;
import com.example.flightreservationsystem.dto.PassengerDto;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock private ReservationRepository reservationRepository;
    @Mock private FlightRepository flightRepository;
    @Mock private PassengerRepository passengerRepository;
    @Mock private ReservationMapper reservationMapper;
    @Mock private PassengerMapper passengerMapper;
    @Mock private FlightMapper flightMapper;
    @Mock private MailService mailService;

    @InjectMocks
    private ReservationService reservationService;

    // ---- helpers ----
    private PassengerEntity passenger(Long id, String email) {
        PassengerEntity p = new PassengerEntity();
        p.setId(id);
        p.setEmail(email);              // <-- DÜZƏLDİLDİ (email -> gmail)
        p.setFirstName("A");
        p.setLastName("B");
        return p;
    }

    private FlightEntity flight(Long id, LocalDateTime dep) {
        FlightEntity f = new FlightEntity();
        f.setId(id);
        f.setDepartureTime(dep);
        f.setDestination("DXB");
        f.setFlightNumber("AZ123");
        return f;
    }

    private ReservationEntity reservation(Long id, PassengerEntity p, FlightEntity f, ReservationStatus st) {
        ReservationEntity r = new ReservationEntity();
        r.setId(id);
        r.setPassenger(p);
        r.setFlight(f);
        r.setStatus(st);
        r.setReservationDate(LocalDateTime.now().minusDays(1));
        return r;
    }

    private ReservationDto dto(Long id, Long pId, Long fId, ReservationStatus st) {
        ReservationDto d = new ReservationDto();
        d.setId(id);
        d.setPassengerId(pId);
        d.setFlightId(fId);
        d.setStatus(st);
        return d;
    }

    // ----- tests -----

    @Test
    @DisplayName("getAllReservations(): bütün rezervasiyalar DTO-ya map olunur")
    void getAllReservations_ok() {
        ReservationEntity r1 = reservation(1L, passenger(10L, "a@a.az"), flight(20L, LocalDateTime.now().plusDays(1)), ReservationStatus.PENDING);
        ReservationEntity r2 = reservation(2L, passenger(11L, "b@a.az"), flight(21L, LocalDateTime.now().plusDays(2)), ReservationStatus.CONFIRMED);
        when(reservationRepository.findAll()).thenReturn(List.of(r1, r2));
        when(reservationMapper.toDto(r1)).thenReturn(dto(1L, 10L, 20L, ReservationStatus.PENDING));
        when(reservationMapper.toDto(r2)).thenReturn(dto(2L, 11L, 21L, ReservationStatus.CONFIRMED));

        List<ReservationDto> out = reservationService.getAllReservations();

        assertEquals(2, out.size());
        verify(reservationRepository).findAll();
        verify(reservationMapper, times(2)).toDto(any());
    }

    @Test
    @DisplayName("getReservationOrThrow(): tapılırsa DTO qaytarır, tapılmırsa ResourceNotFoundException")
    void getReservationOrThrow_found_and_notFound() {
        ReservationEntity r = reservation(5L, passenger(1L, "x@x.az"), flight(2L, LocalDateTime.now().plusDays(1)), ReservationStatus.PENDING);
        when(reservationRepository.findById(5L)).thenReturn(Optional.of(r));
        when(reservationMapper.toDto(r)).thenReturn(dto(5L, 1L, 2L, ReservationStatus.PENDING));

        ReservationDto d = reservationService.getReservationOrThrow(5L);
        assertEquals(5L, d.getId());

        when(reservationRepository.findById(6L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> reservationService.getReservationOrThrow(6L));
    }

    @Nested
    @DisplayName("createReservation()")
    class CreateReservationTests {

        @Test
        @DisplayName("happy path: PENDING status default, save çağırılır, mail göndərilir")
        void create_ok() {
            var p = passenger(100L, "p@p.az");
            var f = flight(200L, LocalDateTime.now().plusHours(5));
            var inDto = dto(null, p.getId(), f.getId(), null);

            when(passengerRepository.findById(p.getId())).thenReturn(Optional.of(p));
            when(flightRepository.findById(f.getId())).thenReturn(Optional.of(f));

            // map to entity
            ReservationEntity ent = reservation(null, null, null, null);
            when(reservationMapper.toEntity(inDto)).thenReturn(ent);

            // save returns entity with id
            ReservationEntity saved = reservation(300L, p, f, ReservationStatus.PENDING);
            saved.setReservationDate(LocalDateTime.now());
            when(reservationRepository.save(any())).thenReturn(saved);

            // map back
            when(reservationMapper.toDto(saved)).thenReturn(dto(300L, p.getId(), f.getId(), ReservationStatus.PENDING));

            when(passengerMapper.toDto(p)).thenReturn(new PassengerDto());
            when(flightMapper.toDto(f)).thenReturn(new FlightDto());

            ReservationDto out = reservationService.createReservation(inDto);

            assertEquals(300L, out.getId());
            // entity-nin PENDING-ə set olunmasını və passenger/flight bağlanmasını yoxlayaq
            ArgumentCaptor<ReservationEntity> cap = ArgumentCaptor.forClass(ReservationEntity.class);
            verify(reservationRepository).save(cap.capture());
            assertEquals(ReservationStatus.PENDING, cap.getValue().getStatus());
            assertSame(p, cap.getValue().getPassenger());
            assertSame(f, cap.getValue().getFlight());
            assertNotNull(cap.getValue().getReservationDate());

            verify(mailService).sendReservationConfirmationMail(any(), any());
        }

        @Test
        @DisplayName("Keçmiş uçuş üçün yaratmaq olmaz → IllegalStateException")
        void create_pastFlight_illegal() {
            var p = passenger(1L, "a@a.az");
            var f = flight(2L, LocalDateTime.now().minusHours(1));
            var inDto = dto(null, p.getId(), f.getId(), null);

            when(passengerRepository.findById(p.getId())).thenReturn(Optional.of(p));
            when(flightRepository.findById(f.getId())).thenReturn(Optional.of(f));

            assertThrows(IllegalStateException.class, () -> reservationService.createReservation(inDto));
            verify(reservationRepository, never()).save(any());
        }

        @Test
        @DisplayName("Passenger/Flight tapılmasa ResourceNotFoundException")
        void create_notFound_refs() {
            var inDto = dto(null, 10L, 20L, null);
            when(passengerRepository.findById(10L)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> reservationService.createReservation(inDto));

            when(passengerRepository.findById(10L)).thenReturn(Optional.of(passenger(10L, "a@a.az")));
            when(flightRepository.findById(20L)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> reservationService.createReservation(inDto));
        }
    }

    @Nested
    @DisplayName("updateReservationOrThrow()")
    class UpdateReservationTests {

        @Test
        @DisplayName("happy path: ID qorunur, reservationDate dəyişmir")
        void update_ok() {
            var p = passenger(10L, "p@p.az");
            var f = flight(20L, LocalDateTime.now().plusDays(1));
            var existing = reservation(99L, p, f, ReservationStatus.PENDING);
            LocalDateTime oldDate = existing.getReservationDate();

            when(reservationRepository.findById(99L)).thenReturn(Optional.of(existing));

            var np = passenger(11L, "n@n.az");
            var nf = flight(21L, LocalDateTime.now().plusDays(2));
            when(passengerRepository.findById(11L)).thenReturn(Optional.of(np));
            when(flightRepository.findById(21L)).thenReturn(Optional.of(nf));

            var inDto = dto(null, 11L, 21L, ReservationStatus.CONFIRMED);
            ReservationEntity mapped = reservation(null, null, null, ReservationStatus.CONFIRMED);
            when(reservationMapper.toEntity(inDto)).thenReturn(mapped);

            ReservationEntity saved = reservation(99L, np, nf, ReservationStatus.CONFIRMED);
            saved.setReservationDate(oldDate);
            when(reservationRepository.save(any())).thenReturn(saved);
            when(reservationMapper.toDto(saved)).thenReturn(dto(99L, 11L, 21L, ReservationStatus.CONFIRMED));

            ReservationDto out = reservationService.updateReservationOrThrow(99L, inDto);

            assertEquals(99L, out.getId());
            ArgumentCaptor<ReservationEntity> cap = ArgumentCaptor.forClass(ReservationEntity.class);
            verify(reservationRepository).save(cap.capture());
            assertEquals(99L, cap.getValue().getId());
            assertSame(np, cap.getValue().getPassenger());
            assertSame(nf, cap.getValue().getFlight());
            assertEquals(oldDate, cap.getValue().getReservationDate());
        }

        @Test
        @DisplayName("Past flight-a köçürmək olmaz → IllegalStateException")
        void update_to_pastFlight_illegal() {
            var existing = reservation(1L, passenger(1L, "a@a.az"), flight(2L, LocalDateTime.now().plusDays(1)), ReservationStatus.PENDING);
            when(reservationRepository.findById(1L)).thenReturn(Optional.of(existing));

            when(passengerRepository.findById(5L)).thenReturn(Optional.of(passenger(5L, "b@a.az")));
            when(flightRepository.findById(6L)).thenReturn(Optional.of(flight(6L, LocalDateTime.now().minusHours(2))));

            assertThrows(IllegalStateException.class,
                    () -> reservationService.updateReservationOrThrow(1L, dto(null, 5L, 6L, ReservationStatus.PENDING)));
        }

        @Test
        @DisplayName("Reservation/Passenger/Flight tapılmadıqda: ResourceNotFoundException")
        void update_notFound_refs() {
            when(reservationRepository.findById(1L)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class,
                    () -> reservationService.updateReservationOrThrow(1L, dto(null, 1L, 1L, null)));

            when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation(1L, null, null, null)));
            when(passengerRepository.findById(10L)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class,
                    () -> reservationService.updateReservationOrThrow(1L, dto(null, 10L, 20L, null)));

            when(passengerRepository.findById(10L)).thenReturn(Optional.of(passenger(10L, "x@x.az")));
            when(flightRepository.findById(20L)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class,
                    () -> reservationService.updateReservationOrThrow(1L, dto(null, 10L, 20L, null)));
        }
    }

    @Nested
    @DisplayName("deleteReservation()")
    class DeleteReservationTests {

        @Test
        @DisplayName("mövcuddursa silinir")
        void delete_ok() {
            when(reservationRepository.existsById(7L)).thenReturn(true);
            reservationService.deleteReservation(7L);
            verify(reservationRepository).deleteById(7L);
        }

        @Test
        @DisplayName("yoxdursa: ResourceNotFoundException")
        void delete_notFound() {
            when(reservationRepository.existsById(7L)).thenReturn(false);
            assertThrows(ResourceNotFoundException.class, () -> reservationService.deleteReservation(7L));
        }
    }

    @Test
    @DisplayName("updateReservationStatus(): status set + save + map")
    void updateStatus_ok() {
        var r = reservation(1L, passenger(1L, "a@a.az"), flight(2L, LocalDateTime.now().plusDays(1)), ReservationStatus.PENDING);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(r));
        var saved = reservation(1L, r.getPassenger(), r.getFlight(), ReservationStatus.CONFIRMED);
        when(reservationRepository.save(any())).thenReturn(saved);
        when(reservationMapper.toDto(saved)).thenReturn(dto(1L, 1L, 2L, ReservationStatus.CONFIRMED));

        ReservationDto out = reservationService.updateReservationStatus(1L, ReservationStatus.CONFIRMED);
        assertEquals(ReservationStatus.CONFIRMED, out.getStatus());
        verify(reservationRepository).save(r);
    }

    @Nested
    @DisplayName("cancelReservation()")
    class CancelReservationTests {
        @Test
        @DisplayName("artıq CANCELLED isə sadəcə DTO qaytarır (save yoxdur)")
        void alreadyCancelled_returns() {
            var r = reservation(1L, passenger(1L, "a@a.az"), flight(2L, LocalDateTime.now().plusDays(1)), ReservationStatus.CANCELLED);
            when(reservationRepository.findById(1L)).thenReturn(Optional.of(r));
            when(reservationMapper.toDto(r)).thenReturn(dto(1L, 1L, 2L, ReservationStatus.CANCELLED));

            ReservationDto out = reservationService.cancelReservation(1L);
            assertEquals(ReservationStatus.CANCELLED, out.getStatus());
            verify(reservationRepository, never()).save(any());
        }

        @Test
        @DisplayName("CONFIRMED/PENDING → CANCELLED və save olunur")
        void confirmed_or_pending_to_cancelled() {
            var r = reservation(2L, passenger(1L, "a@a.az"), flight(2L, LocalDateTime.now().plusDays(1)), ReservationStatus.CONFIRMED);
            when(reservationRepository.findById(2L)).thenReturn(Optional.of(r));
            var saved = reservation(2L, r.getPassenger(), r.getFlight(), ReservationStatus.CANCELLED);
            when(reservationRepository.save(any())).thenReturn(saved);
            when(reservationMapper.toDto(saved)).thenReturn(dto(2L, 1L, 2L, ReservationStatus.CANCELLED));

            ReservationDto out = reservationService.cancelReservation(2L);
            assertEquals(ReservationStatus.CANCELLED, out.getStatus());
            verify(reservationRepository).save(r);
        }

        @Test
        @DisplayName("Digər statuslar üçün IllegalStateException")
        void other_status_illegal() {
            var r = reservation(3L, passenger(1L, "a@a.az"), flight(2L, LocalDateTime.now().plusDays(1)), ReservationStatus.FAILED);
            when(reservationRepository.findById(3L)).thenReturn(Optional.of(r));
            assertThrows(IllegalStateException.class, () -> reservationService.cancelReservation(3L));
        }
    }

    @Test
    @DisplayName("getReservationHistory(): yalnız CANCELLED qeydləri qaytarılır")
    void history_only_cancelled() {
        var r1 = reservation(1L, null, null, ReservationStatus.CANCELLED);
        var r2 = reservation(2L, null, null, ReservationStatus.CONFIRMED);
        var r3 = reservation(3L, null, null, ReservationStatus.CANCELLED);
        when(reservationRepository.findAll()).thenReturn(List.of(r1, r2, r3));
        when(reservationMapper.toDto(r1)).thenReturn(dto(1L, null, null, ReservationStatus.CANCELLED));
        when(reservationMapper.toDto(r3)).thenReturn(dto(3L, null, null, ReservationStatus.CANCELLED));

        List<ReservationDto> out = reservationService.getReservationHistory();
        assertEquals(2, out.size());
        assertTrue(out.stream().allMatch(d -> d.getStatus() == ReservationStatus.CANCELLED));
    }

    @Test
    @DisplayName("sendUpcomingFlightReminders(): hər rezervasiya üçün mailService çağırılır, biri atsınsa digərləri davam edir")
    void reminders_calls_mail_for_each_even_if_one_fails() {
        var p1 = passenger(1L, "a@a.az");
        var p2 = passenger(2L, "b@a.az");
        var f = flight(9L, LocalDateTime.now().plusDays(1));

        var r1 = reservation(11L, p1, f, ReservationStatus.CONFIRMED);
        var r2 = reservation(12L, p2, f, ReservationStatus.CONFIRMED);

        when(reservationRepository.findTomorrowConfirmedReservations()).thenReturn(List.of(r1, r2));
        when(passengerMapper.toDto(p1)).thenReturn(new PassengerDto());
        when(passengerMapper.toDto(p2)).thenReturn(new PassengerDto());
        when(flightMapper.toDto(f)).thenReturn(new FlightDto());

        // <-- DÜZƏLDİLDİ: void metod üçün ardıcıl stubbing
        doThrow(new RuntimeException("smtp error"))
                .doNothing()
                .when(mailService).sendReminderEmail(any(PassengerDto.class), any(FlightDto.class));

        reservationService.sendUpcomingFlightReminders();

        verify(reservationRepository).findTomorrowConfirmedReservations();
        verify(mailService, atLeast(2)).sendReminderEmail(any(), any());
    }
}
