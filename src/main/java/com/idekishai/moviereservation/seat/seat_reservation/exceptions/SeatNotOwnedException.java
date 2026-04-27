package com.idekishai.moviereservation.seat.seat_reservation.exceptions;

public class SeatNotOwnedException extends RuntimeException {
    public SeatNotOwnedException(int seatReservationId) {
        super("Seat reservation " + seatReservationId + " was not locked by the current user");
    }
}
