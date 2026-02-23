package com.idekishai.moviereservation.movie.mappers;

import com.idekishai.moviereservation.movie.dtos.MovieDTO;
import com.idekishai.moviereservation.movie.entities.Movie;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class MovieMapper {
    public MovieDTO toDto(Movie movie) {
        return new MovieDTO(movie.getMovieId(), movie.getMovieName(), movie.getMovieLength(), movie.getMovieType());
    }

    public Movie toEntity(MovieDTO dto) {
        Movie movie = new Movie();
        movie.setMovieName(dto.movieName());
        movie.setMovieLength(dto.movieLength());
        movie.setMovieType(dto.movieType());
        return movie;
    }

    public List<MovieDTO> toDtoList(List<Movie> movies) {
        return movies.stream()
                .filter(Objects::nonNull)
                .map(this::toDto)
                .toList();
    }
}
