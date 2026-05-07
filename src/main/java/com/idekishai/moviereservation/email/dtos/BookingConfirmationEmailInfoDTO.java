package com.idekishai.moviereservation.email.dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record BookingConfirmationEmailInfoDTO(
        String email,
        String movieName,
        String theatreName,
        String theatreCity,
        String screenName,
        char seatRow,
        int seatColumn,
        LocalDate showtimeDate,
        LocalTime showtimeTime,
        String cardHolderName,
        String lastFourDigits,
        LocalDateTime paidAt
) {
}
