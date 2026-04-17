package com.idekishai.moviereservation.seat.seat_reservation.controllers;

import com.idekishai.moviereservation.seat.seat_reservation.dtos.SeatReservationDTO;
import com.idekishai.moviereservation.seat.seat_reservation.services.SeatReservationService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/seats/reservations")
public class AdminSeatReservationController {
    private final SeatReservationService seatReservationService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SeatReservationDTO>> getReservationsByEmail(@RequestParam @NotBlank @Email String email){
        return ResponseEntity.ok(seatReservationService.getAllReservationsForEmail(email));
    }

    @GetMapping("/{showtimeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SeatReservationDTO>> getReservationsByShowtime(@PathVariable @Positive int showtimeId){
        return ResponseEntity.ok(seatReservationService.getAllReservationsForShowtime(showtimeId));
    }

    @PatchMapping("/{seatReservationId}/cancel")
    public ResponseEntity<SeatReservationDTO> cancelReservation(@PathVariable @Positive int seatReservationId){
        return ResponseEntity.ok(seatReservationService.cancelReservation(seatReservationId));
    }
}
