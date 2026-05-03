package com.idekishai.moviereservation.theatre.dtos;

import java.io.Serializable;

public record TheatreDTO(
        int theatreId,
        String theatreName,
        String theatreAddress,
        String theatreCity
) implements Serializable {
}
