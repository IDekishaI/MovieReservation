package com.idekishai.moviereservation.email.dtos;

import java.time.LocalDate;
import java.time.LocalTime;

public record CancellationEmailInfoDTO(
        String email,
        String movieName,
        String theatreName,
        String theatreCity,
        String screenName,
        char seatRow,
        short seatColumn,
        LocalDate showtimeDate,
        LocalTime showtimeTime
) {
}
