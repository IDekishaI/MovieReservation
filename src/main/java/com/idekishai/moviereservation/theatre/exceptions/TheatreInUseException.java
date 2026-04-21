package com.idekishai.moviereservation.theatre.exceptions;

public class TheatreInUseException extends RuntimeException {
    public TheatreInUseException(int theatreId) {
        super("Theatre with id " + theatreId + " has existing screens and cannot be deleted");
    }
}
