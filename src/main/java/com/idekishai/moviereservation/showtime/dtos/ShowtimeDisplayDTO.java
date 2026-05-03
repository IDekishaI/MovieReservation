package com.idekishai.moviereservation.showtime.dtos;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public record ShowtimeDisplayDTO(
        int showtimeId,
        int movieId,
        String movieName,
        int theatreId,
        String theatreName,
        String theatreAddress,
        String theatreCity,
        String screenName,
        LocalDate showtimeDate,
        LocalTime showtimeTime,
        Float price
) implements Serializable{
}
