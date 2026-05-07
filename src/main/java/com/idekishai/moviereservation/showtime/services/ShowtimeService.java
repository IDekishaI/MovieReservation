package com.idekishai.moviereservation.showtime.services;

import com.idekishai.moviereservation.common.DateUtils;
import com.idekishai.moviereservation.email.dtos.CancellationEmailInfoDTO;
import com.idekishai.moviereservation.email.services.EmailService;
import com.idekishai.moviereservation.movie.entities.Movie;
import com.idekishai.moviereservation.movie.exceptions.MovieNotFoundException;
import com.idekishai.moviereservation.movie.repositories.MovieRepository;
import com.idekishai.moviereservation.screen.entities.Screen;
import com.idekishai.moviereservation.screen.exceptions.ScreenNotFoundException;
import com.idekishai.moviereservation.screen.exceptions.ScreenSchedulingConflictException;
import com.idekishai.moviereservation.screen.repositories.ScreenRepository;
import com.idekishai.moviereservation.seat.enums.ReservationStatus;
import com.idekishai.moviereservation.seat.seat_reservation.entities.SeatReservation;
import com.idekishai.moviereservation.seat.seat_reservation.payment.services.PaymentService;
import com.idekishai.moviereservation.seat.seat_reservation.repositories.SeatReservationRepository;
import com.idekishai.moviereservation.showtime.dtos.ShowtimeDisplayDTO;
import com.idekishai.moviereservation.showtime.dtos.ShowtimeRequestDTO;
import com.idekishai.moviereservation.showtime.entities.Showtime;
import com.idekishai.moviereservation.showtime.exceptions.ShowtimeNotFoundException;
import com.idekishai.moviereservation.showtime.mappers.ShowtimeMapper;
import com.idekishai.moviereservation.showtime.repositories.ShowtimeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShowtimeService {
    private final ShowtimeRepository showtimeRepo;
    private final MovieRepository movieRepository;
    private final ScreenRepository screenRepository;
    private final ShowtimeMapper showtimeMapper;
    private final SeatReservationRepository seatReservationRepository;
    private final EmailService emailService;
    private final PaymentService paymentService;

    @Cacheable(value = "showtimes-by-theatre", key = "#theatreId")
    public List<ShowtimeDisplayDTO> findByTheatreId(int theatreId) {
        return showtimeMapper.toShowtimeDisplayDTOList(showtimeRepo.findAllByTheatreId(theatreId));
    }

    @Cacheable(value = "showtimes-by-movie", key = "#movieId")
    public List<ShowtimeDisplayDTO> findByMovieId(int movieId) {
        return showtimeMapper.toShowtimeDisplayDTOList(showtimeRepo.findByMovie_MovieId(movieId));
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "showtimes-by-theatre", allEntries = true),
            @CacheEvict(value = "showtimes-by-movie", allEntries = true)
    })
    public ShowtimeDisplayDTO saveShowtime(ShowtimeRequestDTO dto) {
        Showtime showtime = new Showtime();

        mapDtoToShowtime(showtime, dto);

        overlapsShowtime(dto, showtime.getMovie().getMovieLength());

        Showtime saved = showtimeRepo.save(showtime);
        return showtimeMapper.toShowtimeDisplayDTO(saved);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "showtimes-by-theatre", allEntries = true),
            @CacheEvict(value = "showtimes-by-movie", allEntries = true)
    })
    public ShowtimeDisplayDTO updateShowtime(int showtimeId, ShowtimeRequestDTO dto) {
        Showtime showtime = showtimeRepo.findById(showtimeId)
                .orElseThrow(() -> new ShowtimeNotFoundException(showtimeId));

        mapDtoToShowtime(showtime, dto);

        overlapsShowtime(dto, showtime.getMovie().getMovieLength());

        Showtime saved = showtimeRepo.save(showtime);
        return showtimeMapper.toShowtimeDisplayDTO(saved);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "showtimes-by-theatre", allEntries = true),
            @CacheEvict(value = "showtimes-by-movie", allEntries = true)
    })
    public void deleteShowtime(int showtimeId) {
        Showtime showtime = showtimeRepo.findById(showtimeId)
                .orElseThrow(() -> new ShowtimeNotFoundException(showtimeId));

        List<SeatReservation> reservations = seatReservationRepository.findByShowtime_ShowtimeId(showtimeId);

        List<CancellationEmailInfoDTO> emailsToNotify = new ArrayList<>();

        for (SeatReservation reservation : reservations) {
            if(reservation.getStatus() == ReservationStatus.BOOKED) {
                paymentService.deletePayment(reservation.getSeatReservationId());

                emailsToNotify.add(new CancellationEmailInfoDTO(
                        reservation.getLockedBy(),
                        showtime.getMovie().getMovieName(),
                        showtime.getTheatre().getTheatreName(),
                        showtime.getTheatre().getTheatreCity(),
                        showtime.getScreen().getScreenName(),
                        reservation.getSeat().getSeatRow(),
                        reservation.getSeat().getSeatColumn(),
                        showtime.getShowtimeDate(),
                        showtime.getShowtimeTime()
                ));
            }
        }

        seatReservationRepository.deleteAll(reservations);

        showtimeRepo.deleteById(showtimeId);

        for(CancellationEmailInfoDTO info : emailsToNotify)
            emailService.sendShowtimeCancellationEmail(info);

        log.info("Showtime {} deleted, {} reservations cancelled", showtimeId, reservations.size());
    }

    private void mapDtoToShowtime(Showtime showtime, ShowtimeRequestDTO dto) {
        Movie movie = movieRepository.findById(dto.movieId())
                .orElseThrow(() -> new MovieNotFoundException(dto.movieId()));
        showtime.setMovie(movie);

        Screen screen = screenRepository.findById(dto.screenId())
                .orElseThrow(() -> new ScreenNotFoundException(dto.screenId()));
        showtime.setScreen(screen);

        showtime.setShowtimeDate(DateUtils.parseStringToLocalDate(dto.showtimeDate()));
        showtime.setShowtimeTime(DateUtils.parseStringToLocalTime(dto.showtimeTime()));
        showtime.setPrice(dto.price());
    }

    private void overlapsShowtime(ShowtimeRequestDTO dto, int movieLength) {
        LocalDate showtimeDate = DateUtils.parseStringToLocalDate(dto.showtimeDate());
        LocalTime showtimeTime = DateUtils.parseStringToLocalTime(dto.showtimeTime());
        Showtime previousShowtime = showtimeRepo
                .findClosestPastShowtime(dto.screenId(), showtimeDate, showtimeTime, PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .orElse(null);

        LocalDateTime showtimeDateTime = LocalDateTime.of(showtimeDate, showtimeTime);

        if (previousShowtime != null) {
            LocalDateTime previousShowtimeDateTime = LocalDateTime.of(previousShowtime.getShowtimeDate(), previousShowtime.getShowtimeTime());

            if (previousShowtimeDateTime.plusMinutes(previousShowtime.getMovie().getMovieLength()).isAfter(showtimeDateTime))
                throw new ScreenSchedulingConflictException(dto.screenId());
        }

        Showtime futureShowtime = showtimeRepo
                .findClosestFutureShowtime(dto.screenId(), showtimeDate, showtimeTime, PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .orElse(null);
        if (futureShowtime != null) {
            LocalDateTime futureShowtimeDateTime = LocalDateTime.of(futureShowtime.getShowtimeDate(), futureShowtime.getShowtimeTime());

            if (showtimeDateTime.plusMinutes(movieLength).isAfter(futureShowtimeDateTime))
                throw new ScreenSchedulingConflictException(dto.screenId());
        }

    }
}
