package com.idekishai.moviereservation.screen.exceptions;

public class ScreenAlreadyExistsException extends RuntimeException {
    public ScreenAlreadyExistsException(String screenName, int theatreId) {
        super("Screen with name \"" + screenName + "\" already exists at theatre " + theatreId);
    }
}
