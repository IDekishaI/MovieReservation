package com.idekishai.moviereservation.showtime.controllers;

import com.idekishai.moviereservation.seat.dtos.SeatAvailabilityDTO;
import com.idekishai.moviereservation.seat.services.SeatService;
import com.idekishai.moviereservation.showtime.dtos.ShowtimeDisplayDTO;
import com.idekishai.moviereservation.showtime.services.ShowtimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/showtimes")
public class ShowtimeController {
    private final ShowtimeService showtimeService;
    private final SeatService seatService;

    @GetMapping("/theatre/{theatreId}")
    public ResponseEntity<List<ShowtimeDisplayDTO>> getShowtimesByTheatre(@PathVariable int theatreId) {
        return ResponseEntity.ok(showtimeService.findByTheatreId(theatreId));
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<ShowtimeDisplayDTO>> getShowtimesByMovie(@PathVariable int movieId) {
        return ResponseEntity.ok(showtimeService.findByMovieId(movieId));
    }

    @GetMapping("/{showtimeId}")
    public ResponseEntity<List<SeatAvailabilityDTO>> getAvailableSeatsForShowtime(@PathVariable int showtimeId) {
        return ResponseEntity.ok(seatService.getAvailableSeats(showtimeId));
    }
}
