package com.idekishai.moviereservation.seat.repositories;

import com.idekishai.moviereservation.seat.entities.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Integer> {
    @Query("""
                    SELECT COUNT(s) > 0
                    FROM SeatReservation s
                    WHERE s.seat.seatId = :seatId
            """)
    boolean existsInSeat_Reservation(int seatId);

    boolean existsByScreen_screenIdAndSeatRowAndSeatColumn(int screenId, char seatRow, int seatColumn);

    List<Seat> findByScreen_ScreenIdAndInUseTrue(int screenId);
}
