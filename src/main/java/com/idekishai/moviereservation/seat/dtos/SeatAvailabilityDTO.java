package com.idekishai.moviereservation.seat.dtos;

import com.idekishai.moviereservation.seat.enums.SeatStatus;
import com.idekishai.moviereservation.seat.enums.SeatType;

public record SeatAvailabilityDTO(
        int seatId,
        char seatRow,
        short seatColumn,
        SeatType seatType,
        SeatStatus status) {
}
