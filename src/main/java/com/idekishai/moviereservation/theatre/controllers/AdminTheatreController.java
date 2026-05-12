package com.idekishai.moviereservation.theatre.controllers;

import com.idekishai.moviereservation.theatre.dtos.TheatreDTO;
import com.idekishai.moviereservation.theatre.dtos.TheatreRequestDTO;
import com.idekishai.moviereservation.theatre.services.TheatreService;
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
@Validated
@RequestMapping("/theatres")
public class AdminTheatreController {
    private final TheatreService theatreService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TheatreDTO> saveTheatre(@Valid @RequestBody TheatreRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(theatreService.saveTheatre(dto));
    }

    @PutMapping("/{theatreId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TheatreDTO> updateTheatre(@PathVariable @Positive int theatreId, @Valid @RequestBody TheatreRequestDTO dto) {
        return ResponseEntity.ok(theatreService.updateTheatre(theatreId, dto));
    }

    @DeleteMapping("/{theatreId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteTheatre(@PathVariable @Positive int theatreId) {
        theatreService.deleteTheatre(theatreId);
        return ResponseEntity.ok("Theatre deleted successfully");
    }
}
