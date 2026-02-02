package com.idekishai.moviereservation.showtime.services;

import com.idekishai.moviereservation.showtime.dtos.ShowtimeDisplayDTO;
import com.idekishai.moviereservation.showtime.mappers.ShowtimeMapper;
import com.idekishai.moviereservation.showtime.repositories.ShowtimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShowtimeService {
    private final ShowtimeRepository showtimeRepo;
    private final ShowtimeMapper showtimeMapper;

    public List<ShowtimeDisplayDTO> findByTheatreId(int theatreId){
        return showtimeMapper.toShowtimeDisplayDTOList(showtimeRepo.findAllByTheatreId(theatreId));
    }
    public List<ShowtimeDisplayDTO> findByMovieId(int movieId){
        return showtimeMapper.toShowtimeDisplayDTOList(showtimeRepo.findByMovie_MovieId(movieId));
    }
}
