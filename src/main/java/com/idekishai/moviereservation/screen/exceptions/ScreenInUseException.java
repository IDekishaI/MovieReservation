package com.idekishai.moviereservation.screen.exceptions;

public class ScreenInUseException extends RuntimeException {
    public ScreenInUseException(int screenId) {
        super("Screen with id " + screenId + " is being used in showtimes and cannot be deleted");
    }
}
