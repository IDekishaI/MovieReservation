package com.idekishai.moviereservation.seat.seat_reservation.dtos;

public record ReservationRequestDTO(
        int showtimeId,
        int seatId
) {
}
