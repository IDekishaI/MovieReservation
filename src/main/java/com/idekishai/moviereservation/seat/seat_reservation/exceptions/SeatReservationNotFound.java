package com.idekishai.moviereservation.seat.seat_reservation.exceptions;

public class SeatReservationNotFound extends RuntimeException {
    public SeatReservationNotFound(int seatReservationId) {
        super("Seat reservation with id " + seatReservationId + " not found");
    }
}
