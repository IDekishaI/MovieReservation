package com.idekishai.moviereservation.seat.seat_reservation.controllers;

import com.idekishai.moviereservation.seat.seat_reservation.dtos.ReservationRequestDTO;
import com.idekishai.moviereservation.seat.seat_reservation.dtos.SeatReservationDTO;
import com.idekishai.moviereservation.seat.seat_reservation.services.SeatReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/seats")
public class SeatReservationController {
    private final SeatReservationService seatReservationService;
    @PostMapping("/lock")
    public ResponseEntity<SeatReservationDTO> lockSeat(@RequestBody ReservationRequestDTO reservationRequestDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(seatReservationService.lockSeat(reservationRequestDTO));
    }
}
