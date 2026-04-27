package com.idekishai.moviereservation.theatre.exceptions;

public class TheatreAlreadyExistsException extends RuntimeException {
    public TheatreAlreadyExistsException(String theatreName, String theatreCity) {
        super("Theatre with name \"" + theatreName + "\" already exists in " + theatreCity);
    }
}
