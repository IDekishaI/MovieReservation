package com.idekishai.moviereservation.seat.seat_reservation.exceptions;

public class SeatAlreadyLockedException extends RuntimeException {
    public SeatAlreadyLockedException(int seatId, int showtimeId) {
        super("Seat " + seatId + " is already locked or booked for showtime + " + showtimeId);
    }
}
