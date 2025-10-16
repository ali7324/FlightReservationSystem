package com.example.flightreservationsystem.service;

import com.example.flightreservationsystem.dto.FlightDto;
import com.example.flightreservationsystem.dto.PassengerDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.lenient;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @Mock private JavaMailSender mailSender;
    @InjectMocks private MailService mailService;

    private PassengerDto mockPassenger(String first, String last, String email) {
        PassengerDto p = mock(PassengerDto.class);
        when(p.getFirstName()).thenReturn(first);
        when(p.getLastName()).thenReturn(last);
        when(p.getEmail()).thenReturn(email);
        return p;
    }

    private FlightDto mockFlight(String no, String destination,
                                 LocalDateTime dep, LocalDateTime arr,
                                 BigDecimal priceOrNull) {
        FlightDto f = mock(FlightDto.class);
        when(f.getFlightNumber()).thenReturn(no);
        when(f.getDestination()).thenReturn(destination);
        when(f.getDepartureTime()).thenReturn(dep);

        lenient().when(f.getArrivalTime()).thenReturn(arr);
        lenient().when(f.getPrice()).thenReturn(priceOrNull);
        return f;
    }

    @Test
    @DisplayName("sendReservationConfirmationMail: uğurla mail göndərilir və mətn doğru formalaşır")
    void sendReservationConfirmationMail_success() {
        PassengerDto passenger = mockPassenger("Ali", "Qafarov", "ali@example.com");
        LocalDateTime dep = LocalDateTime.now().plusDays(1).withSecond(0).withNano(0);
        LocalDateTime arr = dep.plusHours(2);
        FlightDto flight = mockFlight("AZ101", "IST", dep, arr, new BigDecimal("199.99"));

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        mailService.sendReservationConfirmationMail(passenger, flight);

        verify(mailSender).send(captor.capture());
        SimpleMailMessage sent = captor.getValue();

        assertEquals("qafarali91@gmail.com", sent.getFrom());
        assertArrayEquals(new String[]{"ali@example.com"}, sent.getTo());
        assertEquals("Uçuş rezervasiyanız təsdiqləndi", sent.getSubject());
        assertNotNull(sent.getSentDate());

        String text = sent.getText();
        assertNotNull(text);
        assertTrue(text.contains("Hörmətli Ali Qafarov"));
        assertTrue(text.contains("Uçuş nömrəsi: AZ101"));
        assertTrue(text.contains("Təyinat yeri: IST"));
        assertTrue(text.contains(dep.toString()));
        assertTrue(text.contains(arr.toString()));
        assertTrue(text.contains("Qiymət: 199.99 AZN"));
    }

    @Test
    @DisplayName("sendReservationConfirmationMail: price null olduqda 'Qiymət: — AZN'")
    void sendReservationConfirmationMail_nullPrice() {
        PassengerDto passenger = mockPassenger("Leyla", "A.", "leyla@example.com");
        LocalDateTime dep = LocalDateTime.now().plusDays(2).withSecond(0).withNano(0);
        LocalDateTime arr = dep.plusHours(3);
        FlightDto flight = mockFlight("AZ202", "GYD", dep, arr, null);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        mailService.sendReservationConfirmationMail(passenger, flight);
        verify(mailSender).send(captor.capture());

        String text = captor.getValue().getText();
        assertNotNull(text);
        assertTrue(text.contains("Qiymət: — AZN"));
        assertFalse(text.contains("null"));
    }

    @Test
    @DisplayName("sendReservationConfirmationMail: send() exception atanda RuntimeException atılır")
    void sendReservationConfirmationMail_failure_wraps() {
        PassengerDto passenger = mockPassenger("Nigar", "B.", "nigar@example.com");
        LocalDateTime dep = LocalDateTime.now().plusDays(1);
        LocalDateTime arr = dep.plusHours(1);
        FlightDto flight = mockFlight("AZ303", "TBS", dep, arr, new BigDecimal("50"));

        doThrow(new RuntimeException("smtp down")).when(mailSender).send(any(SimpleMailMessage.class));
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> mailService.sendReservationConfirmationMail(passenger, flight));
        assertTrue(ex.getMessage().contains("E-poçt göndərilə bilmədi"));
    }

    @Test
    @DisplayName("sendReminderEmail: uğurla mail göndərilir, subject və mətn düzgündür")
    void sendReminderEmail_success() {
        PassengerDto passenger = mockPassenger("Orxan", "H.", "orxan@example.com");
        LocalDateTime dep = LocalDateTime.now().plusDays(1).withSecond(0).withNano(0);
        FlightDto flight = mockFlight("AZ404", "ESB", dep, dep.plusHours(2), new BigDecimal("120.00"));

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        mailService.sendReminderEmail(passenger, flight);

        verify(mailSender).send(captor.capture());
        SimpleMailMessage sent = captor.getValue();

        assertEquals("qafarali91@gmail.com", sent.getFrom());
        assertArrayEquals(new String[]{"orxan@example.com"}, sent.getTo());
        assertEquals("Uçuş Xatırlatması", sent.getSubject());
        assertNotNull(sent.getSentDate());

        String text = sent.getText();
        assertNotNull(text);
        assertTrue(text.contains("Salam Orxan H."));
        assertTrue(text.contains(dep.toLocalDate().toString()));
        assertTrue(text.contains("AZ404"));
        assertTrue(text.contains("Təyinat: ESB"));
        assertTrue(text.contains(dep.toLocalTime().toString()));
    }
}
