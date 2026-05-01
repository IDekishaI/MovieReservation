package com.idekishai.moviereservation.seat.seat_reservation.controllers;

import com.idekishai.moviereservation.seat.seat_reservation.dtos.SeatReservationDTO;
import com.idekishai.moviereservation.seat.seat_reservation.services.SeatReservationService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/reservations")
public class AdminSeatReservationController {
    private final SeatReservationService seatReservationService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<SeatReservationDTO>> getReservationsByEmail(
            @RequestParam @NotBlank @Email String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(seatReservationService.getAllReservationsForEmail(email, PageRequest.of(page, size)));
    }

    @GetMapping("/{showtimeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SeatReservationDTO>> getReservationsByShowtime(@PathVariable @Positive int showtimeId) {
        return ResponseEntity.ok(seatReservationService.getAllReservationsForShowtime(showtimeId));
    }

    @PatchMapping("/{seatReservationId}/cancel")
    public ResponseEntity<SeatReservationDTO> cancelReservation(@PathVariable @Positive int seatReservationId) {
        return ResponseEntity.ok(seatReservationService.cancelReservation(seatReservationId));
    }
}
