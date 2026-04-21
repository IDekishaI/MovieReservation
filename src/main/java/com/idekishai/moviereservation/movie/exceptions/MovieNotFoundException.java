package com.idekishai.moviereservation.movie.exceptions;

public class MovieNotFoundException extends RuntimeException {
    public MovieNotFoundException(int movieId) {
        super("Movie with id " + movieId + " not found");
    }
}
