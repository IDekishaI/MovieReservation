package com.idekishai.moviereservation.seat.seat_reservation.services;

import com.idekishai.moviereservation.seat.entities.Seat;
import com.idekishai.moviereservation.seat.enums.ReservationStatus;
import com.idekishai.moviereservation.seat.repositories.SeatRepository;
import com.idekishai.moviereservation.seat.seat_reservation.dtos.ReservationRequestDTO;
import com.idekishai.moviereservation.seat.seat_reservation.dtos.SeatReservationDTO;
import com.idekishai.moviereservation.seat.seat_reservation.entities.SeatReservation;
import com.idekishai.moviereservation.seat.seat_reservation.repositories.SeatReservationRepository;
import com.idekishai.moviereservation.showtime.entities.Showtime;
import com.idekishai.moviereservation.showtime.repositories.ShowtimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class SeatReservationService {
    private final SeatReservationRepository seatReservationRepository;
    private final SeatRepository seatRepository;
    private final ShowtimeRepository showtimeRepository;
    public SeatReservationDTO lockSeat(ReservationRequestDTO dto){
        Seat seat = seatRepository.findById(dto.seatId()).orElseThrow(() -> new RuntimeException("Seat with id not found"));

        Showtime showtime = showtimeRepository.findById(dto.showtimeId()).orElseThrow(() -> new RuntimeException("Showtime with id not found"));

        boolean isUnavailable = seatReservationRepository.existsBySeat_SeatIdAndShowtime_ShowtimeIdAndLockedUntilAfter(dto.seatId(), dto.showtimeId(), LocalDateTime.now());
        if(isUnavailable)
            throw new RuntimeException("Seat is locked or booked for this showtime");

        LocalDateTime lockedUntil = LocalDateTime.now().plusMinutes(5);

        SeatReservation reservation = new SeatReservation();
        reservation.setSeat(seat);
        reservation.setShowtime(showtime);
        reservation.setStatus(ReservationStatus.LOCKED);
        reservation.setLockedUntil(lockedUntil);

        SeatReservation saved = seatReservationRepository.save(reservation);

        return new SeatReservationDTO(
                saved.getSeatReservationId(),
                seat.getSeatRow(),
                seat.getSeatColumn(),
                saved.getStatus(),
                saved.getLockedUntil()
        );
    }
}
