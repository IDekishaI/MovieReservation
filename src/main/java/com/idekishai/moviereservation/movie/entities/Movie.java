package com.idekishai.moviereservation.movie.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "movie")
public class Movie {
    @Column(name = "movieId", unique = true)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int movieId;
    @Column(name = "movieName", unique = true)
    String movieName;
    @Column(name = "movieLength", unique = true)
    short movieLength;
    @Column(name = "movieType")
    String movieType;
}
