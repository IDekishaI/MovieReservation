package com.idekishai.moviereservation.movie.dtos;

public record MovieDTO(
        String movieName,
        short movieLength,
        String movieType
) {
}
