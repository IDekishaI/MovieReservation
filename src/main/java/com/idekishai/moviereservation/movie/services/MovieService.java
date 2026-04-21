package com.idekishai.moviereservation.movie.services;

import com.idekishai.moviereservation.movie.dtos.MovieDTO;
import com.idekishai.moviereservation.movie.dtos.MovieRequestDTO;
import com.idekishai.moviereservation.movie.entities.Movie;
import com.idekishai.moviereservation.movie.exceptions.MovieInUseException;
import com.idekishai.moviereservation.movie.exceptions.MovieNotFoundException;
import com.idekishai.moviereservation.movie.mappers.MovieMapper;
import com.idekishai.moviereservation.movie.repositories.MovieRepository;
import jakarta.transaction.Transactional;
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

    @Transactional
    public MovieDTO saveMovie(MovieRequestDTO dto) {
        Movie movie = new Movie();
        movie.setMovieName(dto.movieName().trim());
        movie.setMovieLength(dto.movieLength());
        movie.setMovieType(dto.movieType().trim());

        Movie saved = movieRepo.save(movie);
        return movieMapper.toDto(saved);
    }

    @Transactional
    public MovieDTO updateMovie(int movieId, MovieRequestDTO dto) {
        Movie movie = movieRepo.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException(movieId));

        movie.setMovieName(dto.movieName().trim());
        movie.setMovieLength(dto.movieLength());
        movie.setMovieType(dto.movieType().trim());

        Movie saved = movieRepo.save(movie);
        return movieMapper.toDto(saved);
    }

    @Transactional
    public void deleteMovie(int movieId) {
        if (!movieRepo.existsById(movieId))
            throw new MovieNotFoundException(movieId);

        if (movieRepo.existsInShowtimes(movieId))
            throw new MovieInUseException(movieId);

        movieRepo.deleteById(movieId);
    }
}
