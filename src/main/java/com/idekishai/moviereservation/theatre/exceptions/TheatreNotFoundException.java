package com.idekishai.moviereservation.theatre.exceptions;

public class TheatreNotFoundException extends RuntimeException {
    public TheatreNotFoundException(int theatreId) {
        super("Theatre with id " + theatreId + " not found");
    }
}
