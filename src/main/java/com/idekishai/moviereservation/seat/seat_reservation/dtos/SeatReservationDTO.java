package com.idekishai.moviereservation.seat.seat_reservation.dtos;

import com.idekishai.moviereservation.seat.enums.ReservationStatus;

import java.time.LocalDateTime;

public record SeatReservationDTO(
        int seatReservationId,
        char seatRow,
        int seatColumn,
        ReservationStatus status,
        String lockedBy,
        LocalDateTime lockedUntil
) {
}
