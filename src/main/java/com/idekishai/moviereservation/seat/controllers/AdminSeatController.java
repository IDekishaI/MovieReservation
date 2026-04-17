package com.idekishai.moviereservation.seat.controllers;

import com.idekishai.moviereservation.seat.dtos.SeatDTO;
import com.idekishai.moviereservation.seat.dtos.SeatRequestDTO;
import com.idekishai.moviereservation.seat.services.SeatService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/seats")
public class AdminSeatController {
    private final SeatService seatService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SeatDTO>> getAllSeats() {
        return ResponseEntity.ok(seatService.getAllSeats());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SeatDTO> saveSeat(@Valid @RequestBody SeatRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(seatService.saveSeat(dto));
    }

    @PutMapping("/{seatId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SeatDTO> updateSeat(@PathVariable @Positive int seatId, @Valid @RequestBody SeatRequestDTO dto) {
        return ResponseEntity.ok(seatService.updateSeat(seatId, dto));
    }

    @DeleteMapping("/{seatId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteSeat(@PathVariable @Positive int seatId) {
        seatService.deleteSeat(seatId);
        return ResponseEntity.ok("Seat deleted successfully");
    }
}
