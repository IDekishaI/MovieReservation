package com.idekishai.moviereservation.movie.exceptions;

public class MovieInUseException extends RuntimeException {
    public MovieInUseException(int movieId) {
        super("Movie with id " + movieId + " is being used in existing showtimes and cannot be deleted");
    }
}
