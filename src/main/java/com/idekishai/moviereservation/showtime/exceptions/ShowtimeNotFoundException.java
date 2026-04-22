package com.idekishai.moviereservation.showtime.exceptions;

public class ShowtimeNotFoundException extends RuntimeException {
    public ShowtimeNotFoundException(int showtimeId) {
        super("Showtime with id " + showtimeId + " not found");
    }
}
