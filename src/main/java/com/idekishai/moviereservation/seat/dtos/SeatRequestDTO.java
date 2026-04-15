package com.idekishai.moviereservation.seat.dtos;

import com.idekishai.moviereservation.seat.enums.SeatType;
import jakarta.validation.constraints.*;

public record SeatRequestDTO(
        @Positive(message = "Screen ID must be positive")
        int screenId,
        @NotBlank(message = "Seat row is required")
        @Pattern(regexp = "^[A-Z]$", message = "Seat row must be a single uppercase letter")
        String seatRow,
        @Min(value = 1, message = "Seat column must be at least 1")
        @Max(value = 50, message = "Seat column must not exceed 50")
        short seatColumn,
        @NotNull(message = "Seat type is required")
        SeatType seatType,
        @NotNull(message = "inUse must be specified")
        Boolean inUse
) {
}
