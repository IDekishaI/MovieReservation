package com.idekishai.moviereservation.screen.exceptions;

public class ScreenNotFoundException extends RuntimeException {
    public ScreenNotFoundException(int screenId) {
        super("Screen with id " + screenId + " not found");
    }
}
