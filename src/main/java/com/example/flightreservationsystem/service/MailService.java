package com.example.flightreservationsystem.service;

import com.example.flightreservationsystem.dto.FlightDto;
import com.example.flightreservationsystem.dto.PassengerDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender mailSender;
    private static final String DEFAULT_FROM_EMAIL = "qafarali91@gmail.com";

    public void sendReservationConfirmationMail(PassengerDto passengerDto, FlightDto flightDto) {
        String fullName = passengerDto.getFirstName() + " " + passengerDto.getLastName();

        String messageBody = String.format(
                "Hörmətli %s,\n\nUçuş rezervasiyanız uğurla tamamlandı.\n\nRezervasiya Məlumatları:\n" +
                        "- Uçuş nömrəsi: %s\n- Təyinat yeri: %s\n- Yola düşmə vaxtı: %s\n- Çatma vaxtı: %s\n- Qiymət: %s AZN\n\n" +
                        "Sizə xoş və rahat bir uçuş arzulayırıq!\n\nHörmətlə,\nFlight Reservation komandası",
                fullName,
                flightDto.getFlightNumber(),
                flightDto.getDestination(),
                flightDto.getDepartureTime(),
                flightDto.getArrivalTime(),
                flightDto.getPrice() == null ? "—" : flightDto.getPrice().toPlainString()
        );

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(DEFAULT_FROM_EMAIL);
            message.setTo(passengerDto.getEmail()); // <-- gmail -> email
            message.setSubject("Uçuş rezervasiyanız təsdiqləndi");
            message.setText(messageBody);
            message.setSentDate(new Date());

            mailSender.send(message);
            log.info("Rezervasiya təsdiq maili göndərildi: {}", maskEmail(passengerDto.getEmail()));
        } catch (Exception e) {
            log.error("Mail göndərilmədi: {}", e.getMessage());
            throw new RuntimeException("E-poçt göndərilə bilmədi: " + e.getMessage());
        }
    }
    //xatirlatma sabah ucursan
    public void sendReminderEmail(PassengerDto passengerDto, FlightDto flightDto) {
        String fullName = passengerDto.getFirstName() + " " + passengerDto.getLastName();
        String body = String.format(
                "Salam %s,\n\nXatırlatma: sabah (%s) %s nömrəli uçuşunuza hazır olun.\n" +
                        "Təyinat: %s\nYola düşmə: %s\n\nUğurlu uçuşlar!",
                fullName,
                flightDto.getDepartureTime().toLocalDate(),
                flightDto.getFlightNumber(),
                flightDto.getDestination(),
                flightDto.getDepartureTime().toLocalTime()
        );

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(DEFAULT_FROM_EMAIL);
        message.setTo(passengerDto.getEmail()); // <-- gmail -> email
        message.setSubject("Uçuş Xatırlatması");
        message.setText(body);
        message.setSentDate(new Date());

        mailSender.send(message);
        log.info("Reminder email sent to {}", maskEmail(passengerDto.getEmail()));
    }

    private String maskEmail(String email) {
        if (email == null) return null;
        int at = email.indexOf('@');
        if (at <= 1) return "***" + email.substring(Math.max(0, at));
        return email.charAt(0) + "***" + email.substring(at);
    }
}
