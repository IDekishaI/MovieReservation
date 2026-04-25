package com.idekishai.moviereservation.seat.exceptions;

public class SeatAlreadyExistsException extends RuntimeException {
    public SeatAlreadyExistsException(String row, short column) {
        super("Seat already exists at row " + row + " column " + column);
    }
}
