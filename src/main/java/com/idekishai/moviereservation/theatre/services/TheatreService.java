package com.idekishai.moviereservation.theatre.services;

import com.idekishai.moviereservation.theatre.dtos.TheatreDTO;
import com.idekishai.moviereservation.theatre.dtos.TheatreRequestDTO;
import com.idekishai.moviereservation.theatre.entities.Theatre;
import com.idekishai.moviereservation.theatre.exceptions.TheatreInUseException;
import com.idekishai.moviereservation.theatre.exceptions.TheatreNotFoundException;
import com.idekishai.moviereservation.theatre.mappers.TheatreMapper;
import com.idekishai.moviereservation.theatre.repositories.TheatreRepository;
import jakarta.transaction.Transactional;
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

    @Transactional
    public TheatreDTO saveTheatre(TheatreRequestDTO dto) {
        Theatre theatre = new Theatre();
        theatre.setTheatreName(dto.theatreName());
        theatre.setTheatreAddress(dto.theatreAddress());
        theatre.setTheatreCity(dto.theatreCity());

        Theatre saved = theatreRepo.save(theatre);
        return theatreMapper.toDto(saved);
    }

    @Transactional
    public TheatreDTO updateTheatre(int theatreId, TheatreRequestDTO dto) {
        Theatre theatre = theatreRepo.findById(theatreId)
                .orElseThrow(() -> new TheatreNotFoundException(theatreId));

        theatre.setTheatreName(dto.theatreName().trim());
        theatre.setTheatreAddress(dto.theatreAddress().trim());
        theatre.setTheatreCity(dto.theatreCity().trim());

        Theatre saved = theatreRepo.save(theatre);
        return theatreMapper.toDto(saved);
    }

    @Transactional
    public void deleteTheatre(int theatreId) {
        if (!theatreRepo.existsById(theatreId))
            throw new TheatreNotFoundException(theatreId);

        if (theatreRepo.existsInScreens(theatreId))
            throw new TheatreInUseException(theatreId);

        theatreRepo.deleteById(theatreId);
    }
}
