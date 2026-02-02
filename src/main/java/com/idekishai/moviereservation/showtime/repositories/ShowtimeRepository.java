package com.idekishai.moviereservation.showtime.repositories;

import com.idekishai.moviereservation.showtime.entities.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Integer> {
    @Query("""
            SELECT s
            FROM Showtime s
            JOIN s.screen sc
            JOIN sc.theatre t
            WHERE t.theatreId = :theatreId
            """)
    List<Showtime> findAllByTheatreId(@Param("theatreId") int theatreId);
    List<Showtime> findByMovie_MovieId(int movieId);
}
