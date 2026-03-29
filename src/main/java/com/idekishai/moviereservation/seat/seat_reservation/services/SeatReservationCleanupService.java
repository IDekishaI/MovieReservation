package com.idekishai.moviereservation.seat.seat_reservation.services;

import com.idekishai.moviereservation.seat.seat_reservation.repositories.SeatReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeatReservationCleanupService {
    private final SeatReservationRepository seatReservationRepository;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void cleanupExpiredLocks() {
        int deleted = seatReservationRepository.deleteExpiredLocks(LocalDateTime.now());
        if (deleted > 0)
            log.info("Cleaned up {} expired seat locks", deleted);
    }
}
