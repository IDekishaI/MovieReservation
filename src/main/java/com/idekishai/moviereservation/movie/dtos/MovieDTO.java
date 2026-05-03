package com.idekishai.moviereservation.movie.dtos;

import java.io.Serializable;

public record MovieDTO(
        int movieId,
        String movieName,
        short movieLength,
        String movieType
) implements Serializable {
}
