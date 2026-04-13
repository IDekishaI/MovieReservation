package com.idekishai.moviereservation.showtime.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record ShowtimeRequestDTO(
        @Positive(message = "Movie ID must be positive")
        int movieId,
        @Positive(message = "Screen ID must be positive")
        int screenId,
        @NotBlank
        @Pattern(regexp = "^(\\d{4})-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$", message = "Invalid date format. Use YYYY:MM:DD")
        String showtimeDate,
        @NotBlank
        @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d):([0-5]\\d)$", message = "Invalid time format. Use HH:mm:ss")
        String showtimeTime,
        @Positive(message = "Price must be positive")
        @NotNull(message = "Price cannot be null")
        Float price
) {
}
