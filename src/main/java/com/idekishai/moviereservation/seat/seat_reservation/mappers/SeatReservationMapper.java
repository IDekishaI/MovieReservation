package com.idekishai.moviereservation.seat.seat_reservation.mappers;

import com.idekishai.moviereservation.seat.seat_reservation.dtos.SeatReservationDTO;
import com.idekishai.moviereservation.seat.seat_reservation.entities.SeatReservation;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class SeatReservationMapper {
    public SeatReservationDTO toDto(SeatReservation seatReservation) {
        return new SeatReservationDTO(seatReservation.getSeatReservationId(),
                seatReservation.getSeat().getSeatRow(),
                seatReservation.getSeat().getSeatColumn(),
                seatReservation.getStatus(),
                seatReservation.getLockedBy(),
                seatReservation.getLockedUntil());
    }

    public List<SeatReservationDTO> toDtoList(List<SeatReservation> seatReservations) {
        return seatReservations.stream()
                .filter(Objects::nonNull)
                .map(this::toDto)
                .toList();
    }
}
