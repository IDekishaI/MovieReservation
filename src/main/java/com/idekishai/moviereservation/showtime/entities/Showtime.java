package com.idekishai.moviereservation.showtime.entities;

import com.idekishai.moviereservation.movie.entities.Movie;
import com.idekishai.moviereservation.screen.entities.Screen;
import com.idekishai.moviereservation.theatre.entities.Theatre;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "showtime")
public class Showtime {
    @Column(name = "showtimeId", unique = true)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int showtimeId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movieId", nullable = false)
    Movie movie;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screenId", nullable = false)
    Screen screen;
    @Column(name = "showtimeDate", nullable = false)
    LocalDate showtimeDate;
    @Column(name = "showtimeTime", nullable = false)
    LocalTime showtimeTime;
    @Column(name = "price", nullable = false)
    Float price;

    public Theatre getTheatre(){
        return screen.getTheatre();
    }
}
