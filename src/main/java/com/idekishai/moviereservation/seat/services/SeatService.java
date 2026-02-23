package com.idekishai.moviereservation.seat.services;

import com.idekishai.moviereservation.seat.dtos.SeatAvailabilityDTO;
import com.idekishai.moviereservation.seat.entities.Seat;
import com.idekishai.moviereservation.seat.enums.SeatStatus;
import com.idekishai.moviereservation.seat.repositories.SeatRepository;
import com.idekishai.moviereservation.seat.seat_reservation.repositories.SeatReservationRepository;
import com.idekishai.moviereservation.showtime.entities.Showtime;
import com.idekishai.moviereservation.showtime.repositories.ShowtimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatService {
    private final SeatRepository seatRepository;
    private final SeatReservationRepository seatReservationRepository;
    private final ShowtimeRepository showtimeRepository;

    public List<SeatAvailabilityDTO> getAvailableSeats(int showtimeId) {
        Showtime showtime = showtimeRepository.findById(showtimeId).orElseThrow(() -> new RuntimeException("No showtime Id"));
        List<Seat> allSeats = seatRepository.findByScreen_ScreenIdAndInUseTrue(showtime.getScreen().getScreenId());
        List<Integer> unavailableSeatIds = seatReservationRepository.findUnavailableSeatIds(showtimeId, LocalDateTime.now());
        return allSeats.stream()
                .map(seat -> {
                    SeatStatus status = unavailableSeatIds.contains(seat.getSeatId()) ? SeatStatus.UNAVAILABLE : SeatStatus.AVAILABLE;
                    return new SeatAvailabilityDTO(seat.getSeatId(), seat.getSeatRow(), seat.getSeatColumn(), seat.getSeatType(), status);
                })
                .toList();
    }
}
