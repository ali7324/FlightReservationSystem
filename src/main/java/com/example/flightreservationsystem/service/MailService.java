package com.example.flightreservationsystem.service;

import com.example.flightreservationsystem.dto.MailDto;
import com.example.flightreservationsystem.entity.FlightEntity;
import com.example.flightreservationsystem.entity.PassengerEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {
    private final MailSender mailSender;

    public void sendSimpleMail(MailDto mailDto){
        log.info("actionLog.sendSimpleMail.start with from :{}", mailDto.getFrom());
        var simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(mailDto.getFrom());
        simpleMailMessage.setTo(mailDto.getTo());
        simpleMailMessage.setSubject(mailDto.getSubject());
        simpleMailMessage.setSentDate(new Date());
        simpleMailMessage.setText(mailDto.getText());
        log.info("actionLog.sendSimpleMail.end with from :{}", mailDto.getFrom());

        mailSender.send(simpleMailMessage);
    }


    public void sendReservationConfirmationMail(PassengerEntity passenger, FlightEntity flight) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("qafarali91@gmail.com");
        message.setTo(passenger.getGmail());
        message.setSubject("Rezervasiya Təsdiqləndi");

        String text = String.format(
                "Salam %s %s,\n\nUçuş rezervasiyanız uğurla tamamlandı.\n\nUçuş Məlumatları:\n- Uçuş nömrəsi: %s\n- Gedəcəyiniz yer: %s\n- Yola düşmə: %s\n- Çatma vaxtı: %s\n- Qiymət: %.2f AZN\n\nUğurlu uçuşlar!",
                passenger.getFirstName(),
                passenger.getLastName(),
                flight.getFlightNumber(),
                flight.getDestination(),
                flight.getDepartureTime(),
                flight.getArrivalTime(),
                flight.getPrice()
        );

        message.setText(text);
        message.setSentDate(new Date());

        mailSender.send(message);
        log.info("Rezervasiya maili göndərildi: {}", passenger.getGmail());
    }
}
