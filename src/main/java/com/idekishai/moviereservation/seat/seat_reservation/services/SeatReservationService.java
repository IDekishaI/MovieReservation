package com.idekishai.moviereservation.seat.seat_reservation.services;

import com.idekishai.moviereservation.common.SecurityUtils;
import com.idekishai.moviereservation.seat.entities.Seat;
import com.idekishai.moviereservation.seat.enums.ReservationStatus;
import com.idekishai.moviereservation.seat.repositories.SeatRepository;
import com.idekishai.moviereservation.seat.seat_reservation.dtos.BookingConfirmationDTO;
import com.idekishai.moviereservation.seat.seat_reservation.dtos.BookingRequestDTO;
import com.idekishai.moviereservation.seat.seat_reservation.dtos.ReservationRequestDTO;
import com.idekishai.moviereservation.seat.seat_reservation.dtos.SeatReservationDTO;
import com.idekishai.moviereservation.seat.seat_reservation.entities.SeatReservation;
import com.idekishai.moviereservation.seat.seat_reservation.mappers.SeatReservationMapper;
import com.idekishai.moviereservation.seat.seat_reservation.payment.entities.Payment;
import com.idekishai.moviereservation.seat.seat_reservation.payment.services.PaymentService;
import com.idekishai.moviereservation.seat.seat_reservation.repositories.SeatReservationRepository;
import com.idekishai.moviereservation.showtime.entities.Showtime;
import com.idekishai.moviereservation.showtime.repositories.ShowtimeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class SeatReservationService {
    private final SeatReservationRepository seatReservationRepository;
    private final SeatRepository seatRepository;
    private final ShowtimeRepository showtimeRepository;
    private final PaymentService paymentService;
    private final SeatReservationMapper seatReservationMapper;

    public SeatReservationDTO lockSeat(ReservationRequestDTO dto) {
        Seat seat = seatRepository.findById(dto.seatId()).orElseThrow(() -> new RuntimeException("Seat with id not found"));

        Showtime showtime = showtimeRepository.findById(dto.showtimeId()).orElseThrow(() -> new RuntimeException("Showtime with id not found"));

        if (seat.getScreen().getScreenId() != showtime.getScreen().getScreenId())
            throw new RuntimeException("Seat is not inside this screen");

        boolean isUnavailable = seatReservationRepository.existsBySeat_SeatIdAndShowtime_ShowtimeIdAndLockedUntilAfter(dto.seatId(), dto.showtimeId(), LocalDateTime.now());
        if (isUnavailable)
            throw new RuntimeException("Seat is locked or booked for this showtime");

        String lockedBy = SecurityUtils.getCurrentUserEmail();

        LocalDateTime lockedUntil = LocalDateTime.now().plusMinutes(5);

        SeatReservation reservation = new SeatReservation();
        reservation.setSeat(seat);
        reservation.setShowtime(showtime);
        reservation.setStatus(ReservationStatus.LOCKED);
        reservation.setLockedBy(lockedBy);
        reservation.setLockedUntil(lockedUntil);

        SeatReservation saved = seatReservationRepository.save(reservation);

        log.info("User {} has successfully locked seat {} for showtime {}", lockedBy, seat.getSeatId(), showtime.getShowtimeId());

        return new SeatReservationDTO(
                saved.getSeatReservationId(),
                seat.getSeatRow(),
                seat.getSeatColumn(),
                saved.getStatus(),
                saved.getLockedBy(),
                saved.getLockedUntil()
        );
    }

    @Transactional
    public BookingConfirmationDTO bookSeat(BookingRequestDTO dto) {
        SeatReservation seatReservation = seatReservationRepository.findById(dto.seatReservationId()).orElseThrow(() -> new RuntimeException("Seat Reservation Id doesn't exist"));

        String currentUserEmail = SecurityUtils.getCurrentUserEmail();

        if (seatReservation.getStatus() != ReservationStatus.LOCKED)
            throw new RuntimeException("Seat isn't locked");
        if (seatReservation.getLockedUntil().isBefore(LocalDateTime.now()))
            throw new RuntimeException("Seat reservation lock has expired");
        if (!seatReservation.getLockedBy().equals(currentUserEmail))
            throw new RuntimeException("Seat hasn't been locked by the same user");

        seatReservation.setStatus(ReservationStatus.BOOKED);
        seatReservation.setLockedUntil(null);

        int seatReservationId = seatReservation.getSeatReservationId();
        char seatRow = seatReservation.getSeat().getSeatRow();
        int seatColumn = seatReservation.getSeat().getSeatColumn();
        ReservationStatus reservationStatus = ReservationStatus.BOOKED;
        String movieName = seatReservation.getShowtime().getMovie().getMovieName();
        LocalDate movieDate = seatReservation.getShowtime().getShowtimeDate();
        LocalTime movieTime = seatReservation.getShowtime().getShowtimeTime();
        String cardHolderName = dto.cardHolderName();
        String lastFourDigits = dto.cardNumber().substring(dto.cardNumber().length() - 4);

        seatReservationRepository.save(seatReservation);

        log.info("User {} has successfully booked seat {} for showtime {}", currentUserEmail, seatReservation.getSeat().getSeatId(), seatReservation.getShowtime().getShowtimeId());

        Payment payment = paymentService.savePayment(dto);

        return new BookingConfirmationDTO(seatReservationId,
                seatRow,
                seatColumn,
                reservationStatus,
                movieName,
                movieDate,
                movieTime,
                cardHolderName,
                lastFourDigits,
                payment.getPaidAt());
    }

    public List<SeatReservationDTO> getAllReservationsForShowtime(int showtimeId) {
        return seatReservationMapper.toDtoList(seatReservationRepository.findByShowtime_ShowtimeId(showtimeId));
    }

    public List<SeatReservationDTO> getAllReservationsForEmail(String email) {
        return seatReservationMapper.toDtoList(seatReservationRepository.findByLockedBy(email));
    }

    @Transactional
    public SeatReservationDTO cancelReservation(int seatReservationId) {
        SeatReservation seatReservation = seatReservationRepository.findById(seatReservationId).orElseThrow(() -> new RuntimeException("Seat Reservation Id doesn't exist"));

        seatReservation.setStatus(ReservationStatus.CANCELED);

        paymentService.deletePayment(seatReservationId);

        SeatReservation saved = seatReservationRepository.save(seatReservation);

        log.info("Seat reservation {} has been successfully canceled", seatReservationId);

        return seatReservationMapper.toDto(saved);
    }
}
