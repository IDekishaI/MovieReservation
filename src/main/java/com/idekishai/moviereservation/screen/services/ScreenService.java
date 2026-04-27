package com.idekishai.moviereservation.screen.services;

import com.idekishai.moviereservation.screen.dtos.ScreenDTO;
import com.idekishai.moviereservation.screen.dtos.ScreenRequestDTO;
import com.idekishai.moviereservation.screen.entities.Screen;
import com.idekishai.moviereservation.screen.exceptions.ScreenAlreadyExistsException;
import com.idekishai.moviereservation.screen.exceptions.ScreenInUseException;
import com.idekishai.moviereservation.screen.exceptions.ScreenNotFoundException;
import com.idekishai.moviereservation.screen.mappers.ScreenMapper;
import com.idekishai.moviereservation.screen.repositories.ScreenRepository;
import com.idekishai.moviereservation.theatre.entities.Theatre;
import com.idekishai.moviereservation.theatre.exceptions.TheatreNotFoundException;
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
        if(screenRepository.existsByScreenNameAndTheatre_TheatreId(dto.screenName().trim(), dto.theatreId()))
            throw new ScreenAlreadyExistsException(dto.screenName().trim(), dto.theatreId());

        Screen screen = new Screen();

        mapDtoToScreen(screen, dto);

        Screen saved = screenRepository.save(screen);
        return screenMapper.toDto(saved);
    }

    @Transactional
    public ScreenDTO updateScreen(int screenId, ScreenRequestDTO dto){
        Screen screen = screenRepository.findById(screenId)
                .orElseThrow(() -> new ScreenNotFoundException(screenId));

        if(screenRepository.existsByScreenNameAndTheatre_TheatreIdAndScreenIdNot(dto.screenName().trim(), dto.theatreId(), screenId))
            throw new ScreenAlreadyExistsException(dto.screenName().trim(), dto.theatreId());

        mapDtoToScreen(screen, dto);

        Screen saved = screenRepository.save(screen);
        return screenMapper.toDto(saved);
    }

    @Transactional
    public void deleteScreen(int screenId){
        if(!screenRepository.existsById(screenId))
            throw new ScreenNotFoundException(screenId);

        if(screenRepository.existsInShowtimes(screenId))
            throw new ScreenInUseException(screenId);

        screenRepository.deleteById(screenId);
    }

    private void mapDtoToScreen(Screen screen, ScreenRequestDTO dto){
        Theatre theatre = theatreRepository.findById(dto.theatreId())
                .orElseThrow(() -> new TheatreNotFoundException(dto.theatreId()));
        screen.setTheatre(theatre);
        screen.setScreenName(dto.screenName().trim());
        screen.setTotalSeats(dto.totalSeats());
    }
}
