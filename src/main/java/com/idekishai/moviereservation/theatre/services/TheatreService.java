package com.idekishai.moviereservation.theatre.services;

import com.idekishai.moviereservation.theatre.dtos.TheatreDTO;
import com.idekishai.moviereservation.theatre.mappers.TheatreMapper;
import com.idekishai.moviereservation.theatre.repositories.TheatreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TheatreService {
    private final TheatreRepository theatreRepo;
    private final TheatreMapper theatreMapper;

    public List<TheatreDTO> getAllTheatres() {
        return theatreMapper.toDtoList(theatreRepo.findAll());
    }
}
