package com.idekishai.moviereservation.theatre.services;

import com.idekishai.moviereservation.theatre.dtos.TheatreDTO;
import com.idekishai.moviereservation.theatre.dtos.TheatreRequestDTO;
import com.idekishai.moviereservation.theatre.entities.Theatre;
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
                .orElseThrow(() -> new RuntimeException("Theatre with id " + theatreId + " not found"));

        theatre.setTheatreName(dto.theatreName().trim());
        theatre.setTheatreAddress(dto.theatreAddress().trim());
        theatre.setTheatreCity(dto.theatreCity().trim());

        Theatre saved = theatreRepo.save(theatre);
        return theatreMapper.toDto(saved);
    }

    @Transactional
    public void deleteTheatre(int theatreId) {
        if (!theatreRepo.existsById(theatreId))
            throw new RuntimeException("Theatre with id " + theatreId + " not found");

        if (theatreRepo.existsInScreens(theatreId))
            throw new RuntimeException("Theatre has existing screens and cannot be deleted");

        theatreRepo.deleteById(theatreId);
    }
}
