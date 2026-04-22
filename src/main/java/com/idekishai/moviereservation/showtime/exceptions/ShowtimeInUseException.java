package com.idekishai.moviereservation.showtime.exceptions;

public class ShowtimeInUseException extends RuntimeException {
    public ShowtimeInUseException(int showtimeId) {
        super("Showtime with id " + showtimeId + " is being used in existing seat reservations and cannot be deleted");
    }
}
