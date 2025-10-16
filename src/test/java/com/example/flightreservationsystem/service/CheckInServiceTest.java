package com.example.flightreservationsystem.service;

import com.example.flightreservationsystem.dto.BoardingPassDto;
import com.example.flightreservationsystem.dto.CheckInEligibilityDto;
import com.example.flightreservationsystem.entity.ReservationEntity;
import com.example.flightreservationsystem.enums.ReservationStatus;
import com.example.flightreservationsystem.exception.ResourceNotFoundException;
import com.example.flightreservationsystem.repository.ReservationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = Strictness.LENIENT)
class CheckInServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private CheckInService checkInService;

    //helper


    private void stubFindById_ForEligibility(Long id, ReservationStatus status, LocalDateTime departureTime) {
        ReservationEntity r = mock(ReservationEntity.class, Answers.RETURNS_DEEP_STUBS);
        when(r.getStatus()).thenReturn(status);
        when(r.getFlight().getDepartureTime()).thenReturn(departureTime);
        when(reservationRepository.findById(id)).thenReturn(Optional.of(r));
    }

    private ReservationEntity stubFindById_ForSuccessfulCheckIn(Long id, LocalDateTime departureTime) {
        ReservationEntity r = mock(ReservationEntity.class, Answers.RETURNS_DEEP_STUBS);
        when(r.getStatus()).thenReturn(ReservationStatus.CONFIRMED);
        when(r.getFlight().getDepartureTime()).thenReturn(departureTime);

        when(reservationRepository.findById(id))
                .thenReturn(Optional.of(r), Optional.of(r));
        when(reservationRepository.save(any(ReservationEntity.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        return r;
    }

    //tests

    @Nested
    @DisplayName("checkEligibility()")
    class CheckEligibilityTests {

        @Test
        @DisplayName("Reservation tapılmadıqda: ResourceNotFoundException")
        void notFound_throws() {
            Long id = 100L;
            when(reservationRepository.findById(id)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> checkInService.checkEligibility(id));
        }

        @Test
        @DisplayName("Status CONFIRMED deyil: eligible=false + 'Only CONFIRMED...'")
        void statusNotConfirmed_ineligible() {
            Long id = 1L;
            LocalDateTime dep = LocalDateTime.now().plusHours(5);
            stubFindById_ForEligibility(id, ReservationStatus.CANCELLED, dep);

            CheckInEligibilityDto dto = checkInService.checkEligibility(id);

            assertFalse(dto.isEligible());
            assertEquals("Only CONFIRMED reservations can be checked-in", dto.getReason());
            assertEquals(dep.minusHours(24), dto.getWindowStart());
            assertEquals(dep, dto.getWindowEnd());
        }

        @Test
        @DisplayName("Pəncərə açılmayıb (now < dep-24h): eligible=false + 'Check-in window not opened yet'")
        void windowNotOpened_ineligible() {
            Long id = 2L;
            LocalDateTime dep = LocalDateTime.now().plusHours(30);
            stubFindById_ForEligibility(id, ReservationStatus.CONFIRMED, dep);

            CheckInEligibilityDto dto = checkInService.checkEligibility(id);

            assertFalse(dto.isEligible());
            assertEquals("Check-in window not opened yet", dto.getReason());
        }

        @Test
        @DisplayName("Uçuş keçib (now > dep): eligible=false + 'Flight departure time passed'")
        void departurePassed_ineligible() {
            Long id = 3L;
            LocalDateTime dep = LocalDateTime.now().minusMinutes(1);
            stubFindById_ForEligibility(id, ReservationStatus.CONFIRMED, dep);

            CheckInEligibilityDto dto = checkInService.checkEligibility(id);

            assertFalse(dto.isEligible());
            assertEquals("Flight departure time passed", dto.getReason());
        }

        @Test
        @DisplayName("CONFIRMED və pəncərə daxilində: eligible=true, reason=OK")
        void confirmedWithinWindow_eligible() {
            Long id = 4L;
            LocalDateTime dep = LocalDateTime.now().plusHours(6); // dep-24h < now < dep
            stubFindById_ForEligibility(id, ReservationStatus.CONFIRMED, dep);

            CheckInEligibilityDto dto = checkInService.checkEligibility(id);

            assertTrue(dto.isEligible());
            assertEquals("OK", dto.getReason());
            assertEquals(dep.minusHours(24), dto.getWindowStart());
            assertEquals(dep, dto.getWindowEnd());
        }

        @Test
        @DisplayName("CHECKED_IN olarkən də 'Only CONFIRMED...' qaytarılır (mövcud davranış)")
        void alreadyCheckedIn_currentBehavior() {
            Long id = 5L;
            LocalDateTime dep = LocalDateTime.now().plusHours(5);
            stubFindById_ForEligibility(id, ReservationStatus.CHECKED_IN, dep);

            CheckInEligibilityDto dto = checkInService.checkEligibility(id);

            assertFalse(dto.isEligible());
            assertEquals("Only CONFIRMED reservations can be checked-in", dto.getReason());
        }
    }

    @Nested
    @DisplayName("checkIn()")
    class CheckInTests {

        @Test
        @DisplayName("Eligible deyil: IllegalStateException və save çağırılmır")
        void notEligible_throws_andNoSave() {
            Long id = 10L;

            LocalDateTime dep = LocalDateTime.now().plusHours(40);
            stubFindById_ForEligibility(id, ReservationStatus.CONFIRMED, dep);

            IllegalStateException ex = assertThrows(IllegalStateException.class, () -> checkInService.checkIn(id));
            assertTrue(ex.getMessage().startsWith("Not eligible for check-in:"));
            verify(reservationRepository, never()).save(any());
        }

        @Test
        @DisplayName("Uğurlu check-in: status CHECKED_IN, save olunur, BP formatı düzgün")
        void success_updatesStatus_saves_returnsBoardingPass() {
            Long id = 11L;
            LocalDateTime dep = LocalDateTime.now().plusHours(5); // pəncərə açıq
            ReservationEntity r = stubFindById_ForSuccessfulCheckIn(id, dep);

            BoardingPassDto bp = checkInService.checkIn(id);

            assertNotNull(bp);
            assertEquals(id, bp.getReservationId());
            assertNotNull(bp.getBoardingPassCode());
            assertTrue(bp.getBoardingPassCode().startsWith("BP-" + id + "-"));

            verify(r).setStatus(ReservationStatus.CHECKED_IN);
            verify(reservationRepository).save(r);
        }

        @Test
        @DisplayName("checkIn zamanı reservation tapılmırsa: ResourceNotFoundException (ikinci findById)")
        void secondFindById_notFound() {
            Long id = 12L;
            ReservationEntity r = mock(ReservationEntity.class, Answers.RETURNS_DEEP_STUBS);
            when(r.getStatus()).thenReturn(ReservationStatus.CONFIRMED);
            when(r.getFlight().getDepartureTime()).thenReturn(LocalDateTime.now().plusHours(3));

            when(reservationRepository.findById(id))
                    .thenReturn(Optional.of(r))    // eligibility
                    .thenReturn(Optional.empty()); // checkIn

            assertThrows(ResourceNotFoundException.class, () -> checkInService.checkIn(id));
        }
    }
}
