package com.idekishai.moviereservation.showtime.mappers;

import com.idekishai.moviereservation.showtime.dtos.ShowtimeDisplayDTO;
import com.idekishai.moviereservation.showtime.entities.Showtime;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class ShowtimeMapper {
    public ShowtimeDisplayDTO toShowtimeDisplayDTO(Showtime showtime) {
        return new ShowtimeDisplayDTO(
                showtime.getShowtimeId(),
                showtime.getMovie().getMovieId(),
                showtime.getMovie().getMovieName(),
                showtime.getTheatre().getTheatreId(),
                showtime.getTheatre().getTheatreName(),
                showtime.getTheatre().getTheatreAddress(),
                showtime.getTheatre().getTheatreCity(),
                showtime.getScreen().getScreenName(),
                showtime.getShowtimeDate(),
                showtime.getShowtimeTime(),
                showtime.getPrice()
        );
    }

    public List<ShowtimeDisplayDTO> toShowtimeDisplayDTOList(List<Showtime> showtimes) {
        return showtimes.stream()
                .filter(Objects::nonNull)
                .map(this::toShowtimeDisplayDTO)
                .toList();
    }
}
