package com.idekishai.moviereservation.movie.repositories;

import com.idekishai.moviereservation.movie.entities.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Integer> {
    @Query("""
                    SELECT COUNT(s) > 0
                    FROM Showtime s
                    WHERE s.movie.movieId = :movieId
            """)
    boolean existsInShowtimes(@Param("movieId") int movieId);

    boolean existsByMovieName(String movieName);

    boolean existsByMovieNameAndMovieIdNot(String movieName, int movieId);
}
