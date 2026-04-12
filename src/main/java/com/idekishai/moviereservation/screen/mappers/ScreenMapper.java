package com.idekishai.moviereservation.screen.mappers;

import com.idekishai.moviereservation.screen.dtos.ScreenDTO;
import com.idekishai.moviereservation.screen.entities.Screen;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class ScreenMapper {
    public ScreenDTO toDto(Screen screen) {
        return new ScreenDTO(screen.getScreenId(),
                screen.getTheatre().getTheatreId(),
                screen.getTheatre().getTheatreName(),
                screen.getScreenName(),
                screen.getTotalSeats());
    }

    public List<ScreenDTO> toDtoList(List<Screen> screens) {
        return screens.stream()
                .filter(Objects::nonNull)
                .map(this::toDto)
                .toList();
    }
}
