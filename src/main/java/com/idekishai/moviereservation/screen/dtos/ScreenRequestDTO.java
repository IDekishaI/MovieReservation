package com.idekishai.moviereservation.screen.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record ScreenRequestDTO(
        @Positive(message = "Theatre ID must be positive")
        int theatreId,
        @NotBlank(message = "Screen name cannot be blank")
        String screenName,
        @Positive(message = "Total seats must be positive")
        short totalSeats
) {
}
