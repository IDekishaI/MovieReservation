package com.idekishai.moviereservation.movie.exceptions;

public class MovieAlreadyExistsException extends RuntimeException {
    public MovieAlreadyExistsException(String movieName) {
        super("Movie with the name \"" + movieName + "\" already exists");
    }
}
