package com.idekishai.moviereservation.screen.exceptions;

public class ScreenSchedulingConflictException extends RuntimeException {
    public ScreenSchedulingConflictException(int screenId) {
        super("Screen with id " + screenId + " is occupied during that time");
    }
}
