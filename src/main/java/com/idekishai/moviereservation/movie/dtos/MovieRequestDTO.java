package com.idekishai.moviereservation.movie.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record MovieRequestDTO(
        @NotBlank(message = "Movie name cannot be blank")
        String movieName,
        @Positive(message = "Movie length must be positive")
        short movieLength,
        @NotBlank(message = "Movie type cannot be blank")
        String movieType
) {
}