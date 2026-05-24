package com.idekishai.moviereservation.seat.repositories;

import com.idekishai.moviereservation.seat.entities.Seat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Integer> {
    @Query("""
                    SELECT COUNT(s) > 0
                    FROM SeatReservation s
                    WHERE s.seat.seatId = :seatId
            """)
    boolean existsInSeat_Reservation(int seatId);

    boolean existsByScreen_screenIdAndSeatRowAndSeatColumn(int screenId, char seatRow, int seatColumn);

    boolean existsByScreen_screenIdAndSeatRowAndSeatColumnAndSeatIdNot(int screenId, char seatRow, int seatColumn, int seatId);

    List<Seat> findByScreen_ScreenIdAndInUseTrue(int screenId);

    List<Seat> findByScreen_ScreenId(int screenId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Seat s WHERE s.seatId = :seatId")
    Optional<Seat> findByIdWithLock(@Param("seatId") int seatId);
}
