package com.idekishai.moviereservation.theatre.dtos;

import jakarta.validation.constraints.NotBlank;

public record TheatreRequestDTO(
        @NotBlank(message = "Theatre name cannot be blank")
        String theatreName,
        @NotBlank(message = "Theatre address cannot be blank")
        String theatreAddress,
        @NotBlank(message = "Theatre city cannot be blank")
        String theatreCity
) {
}
