package com.idekishai.moviereservation.seat.seat_reservation.exceptions;

public class SeatReservationExpiredException extends RuntimeException {
    public SeatReservationExpiredException(int seatReservationId) {
        super("Seat reservation lock with id " + seatReservationId + " has expired");
    }
}
