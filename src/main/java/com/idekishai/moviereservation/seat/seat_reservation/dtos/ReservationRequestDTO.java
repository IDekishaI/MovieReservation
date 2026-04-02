package com.idekishai.moviereservation.seat.seat_reservation.dtos;

import jakarta.validation.constraints.Positive;

public record ReservationRequestDTO(
        @Positive
        int showtimeId,
        @Positive
        int seatId
) {
}
