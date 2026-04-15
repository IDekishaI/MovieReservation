package com.idekishai.moviereservation.showtime.repositories;

import com.idekishai.moviereservation.showtime.entities.Showtime;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Integer> {
    @Query("""
            SELECT s
            FROM Showtime s
            JOIN FETCH s.movie m
            JOIN FETCH s.screen sc
            JOIN FETCH sc.theatre t
            WHERE t.theatreId = :theatreId
            AND (s.showtimeDate > CURRENT_DATE
                    OR (s.showtimeDate = CURRENT_DATE AND s.showtimeTime > CURRENT_TIME ))
            """)
    List<Showtime> findAllByTheatreId(@Param("theatreId") int theatreId);

    @Query("""
                        SELECT s
                        FROM Showtime s
                        JOIN FETCH s.screen sc
                        JOIN FETCH sc.theatre t
                        JOIN FETCH s.movie m
                        WHERE m.movieId = :movieId
                        AND (s.showtimeDate > CURRENT_DATE
                                OR (s.showtimeDate = CURRENT_DATE AND s.showtimeTime > CURRENT_TIME ))
            """)
    List<Showtime> findByMovie_MovieId(int movieId);

    @Query("""
                        SELECT COUNT(sr) > 0
                        FROM SeatReservation sr
                        WHERE sr.showtime.showtimeId = :showtimeId
            """)
    boolean existsInSeat_Reservations(int showtimeId);

    @SuppressWarnings("JpaQlInspection")
    @Query("""
                SELECT s FROM Showtime s
                WHERE s.screen.screenId = :screenId
                AND (
                    s.showtimeDate < :date
                    OR (s.showtimeDate = :date AND s.showtimeTime < CAST(:time AS TIME))
                )
                ORDER BY s.showtimeDate DESC, s.showtimeTime DESC
            """)
    List<Showtime> findClosestPastShowtime(
            int screenId,
            LocalDate date,
            LocalTime time,
            Pageable pageable
    );
    @SuppressWarnings("JpaQlInspection")
    @Query("""
                SELECT s FROM Showtime s
                WHERE s.screen.screenId = :screenId
                AND (
                    s.showtimeDate > :date
                    OR (s.showtimeDate = :date AND s.showtimeTime > CAST(:time AS TIME))
                )
                ORDER BY s.showtimeDate ASC, s.showtimeTime ASC
            """)
    List<Showtime> findClosestFutureShowtime(
            int screenId,
            LocalDate date,
            LocalTime time,
            Pageable pageable
    );
}
