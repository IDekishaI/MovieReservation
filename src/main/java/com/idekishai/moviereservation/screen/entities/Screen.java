package com.idekishai.moviereservation.screen.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "screen")
public class Screen {
    @Column(name = "screenId", unique = true)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int screenId;
    @Column(name = "theatreId", nullable = false)
    int theatreId;
    @Column(name = "screenName", nullable = false)
    String screenName;
    @Column(name = "totalSeats", nullable = false)
    short totalSeats;
}
