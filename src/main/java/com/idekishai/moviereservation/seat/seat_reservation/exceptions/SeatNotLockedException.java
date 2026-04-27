package com.idekishai.moviereservation.seat.seat_reservation.exceptions;

public class SeatNotLockedException extends RuntimeException {
    public SeatNotLockedException(int seatReservationId) {
        super("Seat reservation " + seatReservationId + " is not in a locked state");
    }
}
