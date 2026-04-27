package com.idekishai.moviereservation.seat.seat_reservation.exceptions;

public class SeatNotInScreenException extends RuntimeException {
    public SeatNotInScreenException(int seatId, int screenId) {
        super("Seat " + seatId + " isn't inside screen " + screenId);
    }
}
