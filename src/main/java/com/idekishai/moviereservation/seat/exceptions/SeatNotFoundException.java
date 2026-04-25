package com.idekishai.moviereservation.seat.exceptions;

public class SeatNotFoundException extends RuntimeException {
    public SeatNotFoundException(int seatId) {
        super("Seat with id " + seatId + " not found");
    }
}
