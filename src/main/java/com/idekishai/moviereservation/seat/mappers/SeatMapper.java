package com.idekishai.moviereservation.seat.mappers;

import com.idekishai.moviereservation.seat.dtos.SeatDTO;
import com.idekishai.moviereservation.seat.entities.Seat;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class SeatMapper {
    public SeatDTO toDto(Seat seat) {
        return new SeatDTO(seat.getSeatId(), seat.getScreen().getScreenId(), seat.getSeatRow(), seat.getSeatColumn(), seat.getSeatType(), seat.getInUse());
    }

    public List<SeatDTO> toDtoList(List<Seat> seats) {
        return seats.stream()
                .filter(Objects::nonNull)
                .map(this::toDto)
                .toList();
    }
}
