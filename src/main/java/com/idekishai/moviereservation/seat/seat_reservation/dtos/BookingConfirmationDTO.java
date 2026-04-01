package com.idekishai.moviereservation.seat.seat_reservation.dtos;

import com.idekishai.moviereservation.seat.enums.ReservationStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record BookingConfirmationDTO(
        int seatReservationId,
        char seatRow,
        int seatColumn,
        ReservationStatus status,
        String movieName,
        LocalDate showtimeDate,
        LocalTime showtimeTime,
        String cardHolderName,
        String lastFourDigits,
        LocalDateTime paidAt
) {
}