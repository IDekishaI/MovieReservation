package com.idekishai.moviereservation.showtime.dtos;

import java.time.LocalDate;
import java.time.LocalTime;

public record ShowtimeDisplayDTO(
        String movieName,
        String theatreName,
        String theatreAddress,
        String theatreCity,
        String screenName,
        LocalDate showtimeDate,
        LocalTime showtimeTime,
        Float price
) {
}
