package com.idekishai.moviereservation.showtime.services;

import com.idekishai.moviereservation.common.DateUtils;
import com.idekishai.moviereservation.movie.entities.Movie;
import com.idekishai.moviereservation.movie.repositories.MovieRepository;
import com.idekishai.moviereservation.screen.entities.Screen;
import com.idekishai.moviereservation.screen.repositories.ScreenRepository;
import com.idekishai.moviereservation.showtime.dtos.ShowtimeDisplayDTO;
import com.idekishai.moviereservation.showtime.dtos.ShowtimeRequestDTO;
import com.idekishai.moviereservation.showtime.entities.Showtime;
import com.idekishai.moviereservation.showtime.mappers.ShowtimeMapper;
import com.idekishai.moviereservation.showtime.repositories.ShowtimeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShowtimeService {
    private final ShowtimeRepository showtimeRepo;
    private final MovieRepository movieRepository;
    private final ScreenRepository screenRepository;
    private final ShowtimeMapper showtimeMapper;

    public List<ShowtimeDisplayDTO> findByTheatreId(int theatreId) {
        return showtimeMapper.toShowtimeDisplayDTOList(showtimeRepo.findAllByTheatreId(theatreId));
    }

    public List<ShowtimeDisplayDTO> findByMovieId(int movieId) {
        return showtimeMapper.toShowtimeDisplayDTOList(showtimeRepo.findByMovie_MovieId(movieId));
    }

    @Transactional
    public ShowtimeDisplayDTO saveShowtime(ShowtimeRequestDTO dto) {
        Showtime showtime = new Showtime();

        mapDtoToShowtime(showtime, dto);

        Showtime saved = showtimeRepo.save(showtime);
        return showtimeMapper.toShowtimeDisplayDTO(saved);
    }

    @Transactional
    public ShowtimeDisplayDTO updateShowtime(int showtimeId, ShowtimeRequestDTO dto) {
        Showtime showtime = showtimeRepo.findById(showtimeId)
                .orElseThrow(() -> new RuntimeException("Showtime with id " + showtimeId + " not found"));

        mapDtoToShowtime(showtime, dto);

        Showtime saved = showtimeRepo.save(showtime);
        return showtimeMapper.toShowtimeDisplayDTO(saved);
    }

    @Transactional
    public void deleteShowtime(int showtimeId) {
        if (!showtimeRepo.existsById(showtimeId))
            throw new RuntimeException("Showtime with id " + showtimeId + " not found");

        if (showtimeRepo.existsInSeat_Reservations(showtimeId))
            throw new RuntimeException("Showtime is being used in existing seat reservations and cannot be deleted");

        showtimeRepo.deleteById(showtimeId);
    }

    private void mapDtoToShowtime(Showtime showtime, ShowtimeRequestDTO dto) {
        Movie movie = movieRepository.findById(dto.movieId())
                .orElseThrow(() -> new RuntimeException("Movie with id " + dto.movieId() + " not found"));
        showtime.setMovie(movie);

        Screen screen = screenRepository.findById(dto.screenId())
                .orElseThrow(() -> new RuntimeException("Screen with id " + dto.screenId() + " not found"));
        showtime.setScreen(screen);

        showtime.setShowtimeDate(DateUtils.parseStringToLocalDate(dto.showtimeDate()));
        showtime.setShowtimeTime(DateUtils.parseStringToLocalTime(dto.showtimeTime()));
        showtime.setPrice(dto.price());
    }
}
