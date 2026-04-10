package com.idekishai.moviereservation.theatre.dtos;

public record TheatreRequestDTO(
        String theatreName,
        String theatreAddress,
        String theatreCity
) {
}
