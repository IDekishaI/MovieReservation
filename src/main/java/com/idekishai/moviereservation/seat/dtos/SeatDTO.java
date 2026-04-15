package com.idekishai.moviereservation.seat.dtos;

import com.idekishai.moviereservation.seat.enums.SeatType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public record SeatDTO(
        int seatId,
        int screenId,
        char seatRow,
        short seatColumn,
        @Enumerated(EnumType.STRING)
        SeatType seatType,
        Boolean inUse
) {
}
