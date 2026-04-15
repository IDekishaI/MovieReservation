package com.idekishai.moviereservation.seat.seat_reservation.controllers;

import com.idekishai.moviereservation.seat.seat_reservation.dtos.BookingConfirmationDTO;
import com.idekishai.moviereservation.seat.seat_reservation.dtos.BookingRequestDTO;
import com.idekishai.moviereservation.seat.seat_reservation.dtos.ReservationRequestDTO;
import com.idekishai.moviereservation.seat.seat_reservation.dtos.SeatReservationDTO;
import com.idekishai.moviereservation.seat.seat_reservation.services.SeatReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/seats")
public class SeatReservationController {
    private final SeatReservationService seatReservationService;

    @PostMapping("/lock")
    public ResponseEntity<SeatReservationDTO> lockSeat(@RequestBody @Valid ReservationRequestDTO reservationRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(seatReservationService.lockSeat(reservationRequestDTO));
    }

    @PatchMapping("/book")
    public ResponseEntity<BookingConfirmationDTO> bookSeat(@RequestBody @Valid BookingRequestDTO bookingRequestDTO) {
        return ResponseEntity.ok(seatReservationService.bookSeat(bookingRequestDTO));
    }
}
