package com.idekishai.moviereservation.seat.seat_reservation.repositories;

import com.idekishai.moviereservation.seat.enums.ReservationStatus;
import com.idekishai.moviereservation.seat.seat_reservation.entities.SeatReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SeatReservationRepository extends JpaRepository<SeatReservation, Integer> {
    @Query("""
                    SELECT sr.seat.seatId FROM SeatReservation sr
                    WHERE sr.showtime.showtimeId = :showtimeId
                    AND (sr.status = 'BOOKED' OR (sr.status = 'LOCKED' AND sr.lockedUntil > :now))
            """)
    List<Integer> findUnavailableSeatIds(@Param("showtimeId") int showtimeId, @Param("now") LocalDateTime now);

    boolean existsBySeat_SeatIdAndShowtime_ShowtimeIdAndLockedUntilAfter(int seatId, int showtimeId, LocalDateTime now);

    @Modifying
    @Query("""
            DELETE FROM SeatReservation sr
            WHERE sr.status = 'LOCKED'
            AND sr.lockedUntil < :now
            """)
    int deleteExpiredLocks(@Param("now") LocalDateTime now);

    List<SeatReservation> findByShowtime_ShowtimeId(int showtimeId);

    List<SeatReservation> findByLockedBy(String email);

    int countByLockedByAndStatus(String lockedBy, ReservationStatus status);
}
