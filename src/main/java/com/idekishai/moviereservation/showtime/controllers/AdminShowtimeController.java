package com.idekishai.moviereservation.showtime.controllers;

import com.idekishai.moviereservation.showtime.dtos.ShowtimeDisplayDTO;
import com.idekishai.moviereservation.showtime.dtos.ShowtimeRequestDTO;
import com.idekishai.moviereservation.showtime.services.ShowtimeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/showtimes")
@Validated
public class AdminShowtimeController {
    private final ShowtimeService showtimeService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShowtimeDisplayDTO> saveTheatre(@Valid @RequestBody ShowtimeRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(showtimeService.saveShowtime(dto));
    }

    @PutMapping("/{showtimeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShowtimeDisplayDTO> updateShowtime(@PathVariable @Positive int showtimeId, @Valid @RequestBody ShowtimeRequestDTO dto) {
        return ResponseEntity.ok(showtimeService.updateShowtime(showtimeId, dto));
    }

    @DeleteMapping("/{showtimeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteShowtime(@PathVariable @Positive int showtimeId) {
        showtimeService.deleteShowtime(showtimeId);
        return ResponseEntity.ok("Showtime deleted successfully");
    }
}
