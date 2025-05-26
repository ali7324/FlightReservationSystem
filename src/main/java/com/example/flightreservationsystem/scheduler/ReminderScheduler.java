package com.example.flightreservationsystem.scheduler;

import com.example.flightreservationsystem.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReminderScheduler {

    private final ReservationService reservationService;

    @Scheduled(cron = "0 0 8 * * *")
    public void runReminderJob() {
        log.info("Running reminder job for upcoming flights...");
        reservationService.sendUpcomingFlightReminders();
    }
}
