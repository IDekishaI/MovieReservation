package com.idekishai.moviereservation.screen.repositories;

import com.idekishai.moviereservation.screen.entities.Screen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ScreenRepository extends JpaRepository<Screen, Integer> {
    @Query("""
                    SELECT COUNT(s) > 0
                    FROM Showtime s
                    WHERE s.screen.screenId = :screenId
            """)
    boolean existsInShowtimes(int screenId);
}
