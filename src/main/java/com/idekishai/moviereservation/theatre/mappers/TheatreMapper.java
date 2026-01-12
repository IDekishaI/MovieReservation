package com.idekishai.moviereservation.theatre.mappers;

import com.idekishai.moviereservation.theatre.dtos.TheatreDTO;
import com.idekishai.moviereservation.theatre.entities.Theatre;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class TheatreMapper {
    public TheatreDTO toDto(Theatre theatre){
        return new TheatreDTO(theatre.getTheatreName(), theatre.getTheatreAddress(), theatre.getTheatreCity());
    }
    public Theatre toEntity(TheatreDTO dto){
        Theatre theatre = new Theatre();
        theatre.setTheatreName(dto.theatreName());
        theatre.setTheatreAddress(dto.theatreAddress());
        theatre.setTheatreCity(dto.theatreCity());
        return theatre;
    }
    public List<TheatreDTO> toDtoList(List<Theatre> theatres){
        return theatres.stream()
                .filter(Objects::nonNull)
                .map(this::toDto)
                .toList();
    }
}
