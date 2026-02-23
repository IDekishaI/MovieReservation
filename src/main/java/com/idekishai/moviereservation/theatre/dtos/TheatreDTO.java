package com.idekishai.moviereservation.theatre.dtos;

public record TheatreDTO(
        int theatreId,
        String theatreName,
        String theatreAddress,
        String theatreCity
) {
}
