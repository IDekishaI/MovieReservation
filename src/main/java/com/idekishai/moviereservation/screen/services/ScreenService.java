package com.idekishai.moviereservation.screen.services;

import com.idekishai.moviereservation.screen.dtos.ScreenDTO;
import com.idekishai.moviereservation.screen.dtos.ScreenRequestDTO;
import com.idekishai.moviereservation.screen.entities.Screen;
import com.idekishai.moviereservation.screen.mappers.ScreenMapper;
import com.idekishai.moviereservation.screen.repositories.ScreenRepository;
import com.idekishai.moviereservation.theatre.entities.Theatre;
import com.idekishai.moviereservation.theatre.repositories.TheatreRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScreenService {
    private final ScreenRepository screenRepository;
    private final ScreenMapper screenMapper;
    private final TheatreRepository theatreRepository;

    public List<ScreenDTO> getAllScreens() {
        return screenMapper.toDtoList(screenRepository.findAll());
    }

    @Transactional
    public ScreenDTO saveScreen(ScreenRequestDTO dto){
        Screen screen = new Screen();

        mapDtoToScreen(screen, dto);

        Screen saved = screenRepository.save(screen);
        return screenMapper.toDto(saved);
    }

    @Transactional
    public ScreenDTO updateScreen(int screenId, ScreenRequestDTO dto){
        Screen screen = screenRepository.findById(screenId)
                .orElseThrow(() -> new RuntimeException("Screen with id " + screenId + " not found"));

        mapDtoToScreen(screen, dto);

        Screen saved = screenRepository.save(screen);
        return screenMapper.toDto(saved);
    }

    @Transactional
    public void deleteScreen(int screenId){
        if(!screenRepository.existsById(screenId))
            throw new RuntimeException("Screen with id " + screenId + " not found");

        if(screenRepository.existsInShowtimes(screenId))
            throw new RuntimeException("Screen is being used in existing showtimes and cannot be deleted");

        screenRepository.deleteById(screenId);
    }

    private void mapDtoToScreen(Screen screen, ScreenRequestDTO dto){
        Theatre theatre = theatreRepository.findById(dto.theatreId())
                .orElseThrow(() -> new RuntimeException("Theatre with id " + dto.theatreId() + " not found"));
        screen.setTheatre(theatre);
        screen.setScreenName(dto.screenName().trim());
        screen.setTotalSeats(dto.totalSeats());
    }
}
