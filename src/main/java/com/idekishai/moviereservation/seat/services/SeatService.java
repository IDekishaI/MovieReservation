package com.idekishai.moviereservation.seat.services;

import com.idekishai.moviereservation.screen.entities.Screen;
import com.idekishai.moviereservation.screen.exceptions.ScreenNotFoundException;
import com.idekishai.moviereservation.screen.repositories.ScreenRepository;
import com.idekishai.moviereservation.seat.dtos.SeatAvailabilityDTO;
import com.idekishai.moviereservation.seat.dtos.SeatDTO;
import com.idekishai.moviereservation.seat.dtos.SeatRequestDTO;
import com.idekishai.moviereservation.seat.entities.Seat;
import com.idekishai.moviereservation.seat.enums.SeatStatus;
import com.idekishai.moviereservation.seat.exceptions.SeatAlreadyExistsException;
import com.idekishai.moviereservation.seat.exceptions.SeatInUseException;
import com.idekishai.moviereservation.seat.exceptions.SeatNotFoundException;
import com.idekishai.moviereservation.seat.mappers.SeatMapper;
import com.idekishai.moviereservation.seat.repositories.SeatRepository;
import com.idekishai.moviereservation.seat.seat_reservation.repositories.SeatReservationRepository;
import com.idekishai.moviereservation.showtime.entities.Showtime;
import com.idekishai.moviereservation.showtime.exceptions.ShowtimeNotFoundException;
import com.idekishai.moviereservation.showtime.repositories.ShowtimeRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatService {
    private final SeatMapper seatMapper;
    private final SeatRepository seatRepository;
    private final ScreenRepository screenRepository;
    private final SeatReservationRepository seatReservationRepository;
    private final ShowtimeRepository showtimeRepository;

    public List<SeatAvailabilityDTO> getAvailableSeats(int showtimeId) {
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new ShowtimeNotFoundException(showtimeId));

        List<Seat> allSeats = seatRepository.findByScreen_ScreenIdAndInUseTrue(showtime.getScreen().getScreenId());

        List<Integer> unavailableSeatIds = seatReservationRepository.findUnavailableSeatIds(showtimeId, LocalDateTime.now());

        return allSeats.stream()
                .map(seat -> {
                    SeatStatus status = unavailableSeatIds.contains(seat.getSeatId()) ? SeatStatus.UNAVAILABLE : SeatStatus.AVAILABLE;
                    return new SeatAvailabilityDTO(seat.getSeatId(), seat.getSeatRow(), seat.getSeatColumn(), seat.getSeatType(), status);
                })
                .toList();
    }

    public List<SeatDTO> getAllSeats() {
        return seatMapper.toDtoList(seatRepository.findAll());
    }

    @Transactional
    public SeatDTO saveSeat(SeatRequestDTO dto) {
        Seat seat = new Seat();

        if (seatRepository.existsByScreen_screenIdAndSeatRowAndSeatColumn(dto.screenId(), dto.seatRow().charAt(0), dto.seatColumn()))
            throw new SeatAlreadyExistsException(dto.seatRow(), dto.seatColumn());

        mapDtoToSeat(seat, dto);

        Seat saved = seatRepository.save(seat);
        return seatMapper.toDto(saved);
    }

    @Transactional
    public SeatDTO updateSeat(int seatId, SeatRequestDTO dto) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new SeatNotFoundException(seatId));

        if (seatRepository.existsByScreen_screenIdAndSeatRowAndSeatColumn(dto.screenId(), dto.seatRow().charAt(0), dto.seatColumn()))
            throw new SeatAlreadyExistsException(dto.seatRow(), dto.seatColumn());

        if (seatRepository.existsInSeat_Reservation(seatId))
            throw new SeatInUseException(seatId);

        mapDtoToSeat(seat, dto);

        Seat saved = seatRepository.save(seat);
        return seatMapper.toDto(saved);
    }

    @Transactional
    public void deleteSeat(int seatId) {
        if (!seatRepository.existsById(seatId))
            throw new SeatNotFoundException(seatId);

        if (seatRepository.existsInSeat_Reservation(seatId))
            throw new SeatInUseException(seatId);

        seatRepository.deleteById(seatId);
    }

    private void mapDtoToSeat(Seat seat, SeatRequestDTO dto) {
        Screen screen = screenRepository.findById(dto.screenId())
                .orElseThrow(() -> new ScreenNotFoundException(dto.screenId()));

        seat.setScreen(screen);
        seat.setSeatRow(dto.seatRow().charAt(0));
        seat.setSeatColumn(dto.seatColumn());
        seat.setSeatType(dto.seatType());
        seat.setInUse(dto.inUse());
    }

    public List<SeatDTO> getSeatsByScreenId(@Positive int screenId) {
        return seatMapper.toDtoList(seatRepository.findByScreen_ScreenId(screenId));
    }
}
