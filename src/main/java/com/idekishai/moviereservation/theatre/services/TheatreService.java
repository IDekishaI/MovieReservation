package com.idekishai.moviereservation.theatre.services;

import com.idekishai.moviereservation.theatre.dtos.TheatreDTO;
import com.idekishai.moviereservation.theatre.dtos.TheatreRequestDTO;
import com.idekishai.moviereservation.theatre.entities.Theatre;
import com.idekishai.moviereservation.theatre.exceptions.TheatreAlreadyExistsException;
import com.idekishai.moviereservation.theatre.exceptions.TheatreInUseException;
import com.idekishai.moviereservation.theatre.exceptions.TheatreNotFoundException;
import com.idekishai.moviereservation.theatre.mappers.TheatreMapper;
import com.idekishai.moviereservation.theatre.repositories.TheatreRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TheatreService {
    private final TheatreRepository theatreRepo;
    private final TheatreMapper theatreMapper;

    @Cacheable(value = "theatres", key = "'all'")
    public List<TheatreDTO> getAllTheatres() {
        return theatreMapper.toDtoList(theatreRepo.findAll());
    }

    @Transactional
    @CacheEvict(value = "theatres", key = "'all'")
    public TheatreDTO saveTheatre(TheatreRequestDTO dto) {
        if (theatreRepo.existsByTheatreNameAndTheatreCity(dto.theatreName().trim(), dto.theatreCity().trim()))
            throw new TheatreAlreadyExistsException(dto.theatreName().trim(), dto.theatreCity().trim());

        Theatre theatre = new Theatre();
        theatre.setTheatreName(dto.theatreName().trim());
        theatre.setTheatreAddress(dto.theatreAddress().trim());
        theatre.setTheatreCity(dto.theatreCity().trim());

        Theatre saved = theatreRepo.save(theatre);
        return theatreMapper.toDto(saved);
    }

    @Transactional
    @CacheEvict(value = "theatres", key = "'all'")
    public TheatreDTO updateTheatre(int theatreId, TheatreRequestDTO dto) {
        Theatre theatre = theatreRepo.findById(theatreId)
                .orElseThrow(() -> new TheatreNotFoundException(theatreId));

        if (theatreRepo.existsByTheatreNameAndTheatreCityAndTheatreIdNot(dto.theatreName().trim(), dto.theatreCity().trim(), theatreId))
            throw new TheatreAlreadyExistsException(dto.theatreName().trim(), dto.theatreCity().trim());

        theatre.setTheatreName(dto.theatreName().trim());
        theatre.setTheatreAddress(dto.theatreAddress().trim());
        theatre.setTheatreCity(dto.theatreCity().trim());

        Theatre saved = theatreRepo.save(theatre);
        return theatreMapper.toDto(saved);
    }

    @Transactional
    @CacheEvict(value = "theatres", key = "'all'")
    public void deleteTheatre(int theatreId) {
        if (!theatreRepo.existsById(theatreId))
            throw new TheatreNotFoundException(theatreId);

        if (theatreRepo.existsInScreens(theatreId))
            throw new TheatreInUseException(theatreId);

        theatreRepo.deleteById(theatreId);
    }
}
