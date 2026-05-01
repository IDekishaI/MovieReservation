package com.idekishai.moviereservation.movie.controllers;

import com.idekishai.moviereservation.movie.dtos.MovieDTO;
import com.idekishai.moviereservation.movie.services.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/movies")
public class MovieController {
    private final MovieService movieService;

    @GetMapping
    public ResponseEntity<Page<MovieDTO>> getAllMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(movieService.getAllMovies(PageRequest.of(page, size)));
    }
}
