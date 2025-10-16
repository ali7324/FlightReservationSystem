package com.example.flightreservationsystem.service;

import com.example.flightreservationsystem.dto.FlightDto;
import com.example.flightreservationsystem.dto.PassengerDto;
import com.example.flightreservationsystem.dto.PaymentDto;
import com.example.flightreservationsystem.entity.PaymentEntity;
import com.example.flightreservationsystem.entity.ReservationEntity;
import com.example.flightreservationsystem.enums.PaymentStatus;
import com.example.flightreservationsystem.enums.ReservationStatus;
import com.example.flightreservationsystem.exception.ResourceNotFoundException;
import com.example.flightreservationsystem.mapper.FlightMapper;
import com.example.flightreservationsystem.mapper.PassengerMapper;
import com.example.flightreservationsystem.repository.PaymentRepository;
import com.example.flightreservationsystem.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = Strictness.LENIENT)
class PaymentServiceTest {

    @Mock private PaymentRepository paymentRepository;
    @Mock private ReservationRepository reservationRepository;
    @Mock private MailService mailService;
    @Mock private PassengerMapper passengerMapper;
    @Mock private FlightMapper flightMapper;

    @InjectMocks
    private PaymentService paymentService;

    private PaymentService spyService;

    @BeforeEach
    void makeSpy() {
        spyService = Mockito.spy(paymentService);
    }


    private ReservationEntity mockReservationBare(Long id, ReservationStatus status) {
        ReservationEntity r = mock(ReservationEntity.class, Answers.RETURNS_DEEP_STUBS);
        when(r.getStatus()).thenReturn(status);
        return r;
    }

    private ReservationEntity mockReservationWithPrice(Long id, ReservationStatus status, Number flightPrice) {
        ReservationEntity r = mockReservationBare(id, status);
        BigDecimal price = (flightPrice instanceof BigDecimal bd) ? bd : BigDecimal.valueOf(flightPrice.doubleValue());
        when(r.getFlight().getPrice()).thenReturn(price);
        return r;
    }

    private PaymentDto paymentDto(Long reservationId, String card, String cvv, String mmYY, BigDecimal amount) {
        PaymentDto dto = new PaymentDto();
        dto.setReservationId(reservationId);
        dto.setCardNumber(card);
        dto.setCvv(cvv);
        dto.setExpiryDate(mmYY);
        dto.setAmount(amount);
        return dto;
    }

    private static String futureMMYY(int monthsAhead) {
        YearMonth ym = YearMonth.now().plusMonths(monthsAhead);
        return String.format("%02d/%02d", ym.getMonthValue(), ym.getYear() % 100);
    }



    @Test
    @DisplayName("Kart maskalanması – save olunan PaymentEntity-də yoxlanır")
    void maskCard_savedEntity() {
        Long rid = 10L;
        ReservationEntity r = mockReservationWithPrice(rid, ReservationStatus.PENDING, new BigDecimal("100"));
        when(reservationRepository.findById(rid)).thenReturn(Optional.of(r));
        when(paymentRepository.save(any(PaymentEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        doReturn(PaymentStatus.PENDING).when(spyService).simulatePaymentStatus();

        PaymentDto in = paymentDto(rid, "4111111111111111", "123", futureMMYY(12), new BigDecimal("150"));
        String msg = spyService.processPayment(in);
        assertTrue(msg.toLowerCase().contains("pending"));

        ArgumentCaptor<PaymentEntity> cap = ArgumentCaptor.forClass(PaymentEntity.class);
        verify(paymentRepository).save(cap.capture());
        assertEquals("**** **** **** 1111", cap.getValue().getCardNumber());
        assertEquals(new BigDecimal("150"), cap.getValue().getAmount());
    }

    @Nested
    @DisplayName("Validasiya və səhvlər")
    class ValidationTests {

        @Test
        @DisplayName("Məbləğ null və ya qiymətdən kiçik → IllegalArgumentException (price stub VAR)")
        void amount_invalid() {
            Long rid = 30L;
            ReservationEntity r = mockReservationWithPrice(rid, ReservationStatus.PENDING, new BigDecimal("200"));
            when(reservationRepository.findById(rid)).thenReturn(Optional.of(r));

            assertThrows(IllegalArgumentException.class,
                    () -> paymentService.processPayment(paymentDto(rid, "4111", "123", futureMMYY(12), null)));

            assertThrows(IllegalArgumentException.class,
                    () -> paymentService.processPayment(paymentDto(rid, "4111", "123", futureMMYY(12), new BigDecimal("199.99"))));

            verify(paymentRepository, never()).save(any());
        }

        @Test
        @DisplayName("Status PENDING deyil → IllegalStateException (price stub YOXDUR)")
        void status_not_pending() {
            Long rid = 12L;
            ReservationEntity r = mockReservationBare(rid, ReservationStatus.CONFIRMED);
            when(reservationRepository.findById(rid)).thenReturn(Optional.of(r));

            assertThrows(IllegalStateException.class,
                    () -> paymentService.processPayment(paymentDto(rid, "4111", "123", futureMMYY(12), new BigDecimal("100"))));

            verify(paymentRepository, never()).save(any());
        }

        @Test
        @DisplayName("Kart müddəti bitib → IllegalArgumentException('Card expired') (price stub YOXDUR)")
        void card_expired() {
            Long rid = 11L;
            ReservationEntity r = mockReservationBare(rid, ReservationStatus.PENDING);
            when(reservationRepository.findById(rid)).thenReturn(Optional.of(r));

            String past = "01/20"; // keçmiş tarix
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> paymentService.processPayment(paymentDto(rid, "4111", "123", past, new BigDecimal("100"))));
            assertTrue(ex.getMessage().toLowerCase().contains("expired"));

            verify(paymentRepository, never()).save(any());
        }

        @Test
        @DisplayName("Reservation tapılmır → ResourceNotFoundException")
        void reservation_not_found() {
            when(reservationRepository.findById(404L)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class,
                    () -> paymentService.processPayment(paymentDto(404L, "4111", "123", futureMMYY(12), new BigDecimal("100"))));
        }
    }

    @Nested
    @DisplayName("Uğurlu axınlar")
    class HappyPathTests {

        @Test
        @DisplayName("SUCCESS → CONFIRMED + mail")
        void success_confirms_and_sends_mail() {
            Long rid = 1L;
            ReservationEntity r = mockReservationWithPrice(rid, ReservationStatus.PENDING, new BigDecimal("100"));
            when(reservationRepository.findById(rid)).thenReturn(Optional.of(r));
            when(paymentRepository.save(any(PaymentEntity.class))).thenAnswer(inv -> inv.getArgument(0));

            when(passengerMapper.toDto(any())).thenReturn(new PassengerDto());
            when(flightMapper.toDto(any())).thenReturn(new FlightDto());

            doReturn(PaymentStatus.SUCCESS).when(spyService).simulatePaymentStatus();

            String msg = spyService.processPayment(paymentDto(rid, "4111111111111111", "123", futureMMYY(6), new BigDecimal("150")));

            assertTrue(msg.toLowerCase().contains("successful"));
            verify(r).setStatus(ReservationStatus.CONFIRMED);
            verify(reservationRepository).save(r);
            verify(mailService).sendReservationConfirmationMail(any(PassengerDto.class), any(FlightDto.class));
        }

        @Test
        @DisplayName("PENDING → status dəyişmir, mail YOX")
        void pending_no_status_change_no_mail() {
            Long rid = 2L;
            ReservationEntity r = mockReservationWithPrice(rid, ReservationStatus.PENDING, new BigDecimal("50"));
            when(reservationRepository.findById(rid)).thenReturn(Optional.of(r));
            when(paymentRepository.save(any(PaymentEntity.class))).thenAnswer(inv -> inv.getArgument(0));
            doReturn(PaymentStatus.PENDING).when(spyService).simulatePaymentStatus();

            String msg = spyService.processPayment(paymentDto(rid, "4111", "123", futureMMYY(3), new BigDecimal("60")));

            assertTrue(msg.toLowerCase().contains("pending"));
            verify(reservationRepository, never()).save(r);
            verify(mailService, never()).sendReservationConfirmationMail(any(), any());
        }

        @Test
        @DisplayName("FAILED → FAILED status, mail YOX")
        void failed_sets_failed_status() {
            Long rid = 3L;
            ReservationEntity r = mockReservationWithPrice(rid, ReservationStatus.PENDING, new BigDecimal("80"));
            when(reservationRepository.findById(rid)).thenReturn(Optional.of(r));
            when(paymentRepository.save(any(PaymentEntity.class))).thenAnswer(inv -> inv.getArgument(0));
            doReturn(PaymentStatus.FAILED).when(spyService).simulatePaymentStatus();

            String msg = spyService.processPayment(paymentDto(rid, "4111", "123", futureMMYY(4), new BigDecimal("90")));

            assertTrue(msg.toLowerCase().contains("failed"));
            verify(r).setStatus(ReservationStatus.FAILED);
            verify(reservationRepository).save(r);
            verify(mailService, never()).sendReservationConfirmationMail(any(), any());
        }
    }
}
