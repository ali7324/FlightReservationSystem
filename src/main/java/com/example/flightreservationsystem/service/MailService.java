package com.example.flightreservationsystem.service;

import com.example.flightreservationsystem.dto.FlightDto;
import com.example.flightreservationsystem.dto.MailDto;
import com.example.flightreservationsystem.dto.PassengerDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender javaMailSender;

    public void sendSimpleMail(MailDto mailDto) {
        log.info("Sending simple mail to: {}", mailDto.getTo());

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailDto.getFrom());
        message.setTo(mailDto.getTo());
        message.setSubject(mailDto.getSubject());
        message.setText(mailDto.getText());
        message.setSentDate(new Date());

        javaMailSender.send(message);
        log.info("Mail sent successfully to: {}", mailDto.getTo());
    }

    public void sendReservationConfirmationMail(PassengerDto passengerDto, FlightDto flightDto) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("qafarali91@gmail.com");
        message.setTo(passengerDto.getGmail());
        message.setSubject("Ucuş Rezervasiyanız Uğurla Tamamlandı");

        String text = String.format(
                "Salam %s %s,\n\nUçuş rezervasiyanız uğurla tamamlandı.\n\nUçuş Məlumatları:\n- Uçuş nömrəsi: %s\n- Gedəcəyiniz yer: %s\n- Yola düşmə: %s\n- Çatma vaxtı: %s\n- Qiymət: %.2f AZN\n\nUğurlu uçuşlar!",
                passengerDto.getFirstName(),
                passengerDto.getLastName(),
                flightDto.getFlightNumber(),
                flightDto.getDestination(),
                flightDto.getDepartureTime(),
                flightDto.getArrivalTime(),
                flightDto.getPrice()
        );

        message.setText(text);
        message.setSentDate(new Date());

        javaMailSender.send(message);
        log.info("Confirmation email sent to {}", passengerDto.getGmail());
    }
}
