package com.idekishai.moviereservation.movie.services;

import com.idekishai.moviereservation.movie.dtos.MovieDTO;
import com.idekishai.moviereservation.movie.mappers.MovieMapper;
import com.idekishai.moviereservation.movie.repositories.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepo;
    private final MovieMapper movieMapper;

    public List<MovieDTO> getAllMovies() {
        return movieMapper.toDtoList(movieRepo.findAll());
    }
}
