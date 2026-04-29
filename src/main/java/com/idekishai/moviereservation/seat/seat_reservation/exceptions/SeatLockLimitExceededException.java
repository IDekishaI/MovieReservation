package com.idekishai.moviereservation.seat.seat_reservation.exceptions;

public class SeatLockLimitExceededException extends RuntimeException {
    public SeatLockLimitExceededException(int limit) {
        super("Maximum allowed active seat locks exceeded (" + limit + ")");
    }
}
