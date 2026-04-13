package com.idekishai.moviereservation.showtime.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.time.LocalTime;

public record ShowtimeRequestDTO(
        @Positive(message = "Movie ID must be positive")
        int movieId,
        @Positive(message = "Screen ID must be positive")
        int screenId,
        @NotNull(message = "Showtime date cannot be null")
        @Future(message = "Showtime date must be in the future")
        LocalDate showtimeDate,
        @NotNull(message = "Showtime time cannot be null")
        @Schema(type = "string", example = "14:30:00")
        LocalTime showtimeTime,
        @Positive(message = "Price must be positive")
        @NotNull(message = "Price cannot be null")
        Float price
) {
}
