package com.idekishai.moviereservation.seat.exceptions;

public class SeatInUseException extends RuntimeException {
    public SeatInUseException(int seatId) {
        super("Seat with id " + seatId + " is being used in seat reservations and cannot be changed");
    }
}
