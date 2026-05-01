package com.idekishai.moviereservation.movie.services;

import com.idekishai.moviereservation.movie.dtos.MovieDTO;
import com.idekishai.moviereservation.movie.dtos.MovieRequestDTO;
import com.idekishai.moviereservation.movie.entities.Movie;
import com.idekishai.moviereservation.movie.exceptions.MovieAlreadyExistsException;
import com.idekishai.moviereservation.movie.exceptions.MovieInUseException;
import com.idekishai.moviereservation.movie.exceptions.MovieNotFoundException;
import com.idekishai.moviereservation.movie.mappers.MovieMapper;
import com.idekishai.moviereservation.movie.repositories.MovieRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepo;
    private final MovieMapper movieMapper;

    public Page<MovieDTO> getAllMovies(Pageable pageable) {
        return movieRepo.findAll(pageable).map(movieMapper::toDto);
    }

    @Transactional
    public MovieDTO saveMovie(MovieRequestDTO dto) {
        if (movieRepo.existsByMovieName(dto.movieName().trim()))
            throw new MovieAlreadyExistsException(dto.movieName().trim());

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

        if (movieRepo.existsByMovieNameAndMovieIdNot(dto.movieName().trim(), movieId))
            throw new MovieAlreadyExistsException(dto.movieName().trim());

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
