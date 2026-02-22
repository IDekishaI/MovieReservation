package com.idekishai.moviereservation.seat.repositories;

import com.idekishai.moviereservation.seat.entities.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Integer> {
    List<Seat> findByScreen_ScreenIdAndInUseTrue(int screenId);
}
