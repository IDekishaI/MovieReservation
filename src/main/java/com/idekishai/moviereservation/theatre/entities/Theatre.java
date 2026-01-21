package com.idekishai.moviereservation.theatre.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "theatre")
public class Theatre {
    @Column(name = "theatreId", unique = true)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int theatreId;
    @Column(name = "theatreName", nullable = false)
    String theatreName;
    @Column(name = "theatreAddress", nullable = false)
    String theatreAddress;
    @Column(name = "theatreCity", nullable = false)
    String theatreCity;
}
