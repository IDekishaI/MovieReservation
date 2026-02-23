package com.idekishai.moviereservation.movie.dtos;

public record MovieDTO(
        int movieId,
        String movieName,
        short movieLength,
        String movieType
) {
}
