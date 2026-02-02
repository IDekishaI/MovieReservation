package com.idekishai.moviereservation.screen.entities;

import com.idekishai.moviereservation.theatre.entities.Theatre;
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
    @ManyToOne
    @JoinColumn(name = "theatreId")
    Theatre theatre;
    @Column(name = "screenName", nullable = false)
    String screenName;
    @Column(name = "totalSeats", nullable = false)
    short totalSeats;
}
