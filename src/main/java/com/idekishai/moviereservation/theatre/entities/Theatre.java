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
    @Column(name = "theatreName", unique = true)
    String theatreName;
    @Column(name = "theatreAddress", unique = true)
    String theatreAddress;
    @Column(name = "theatreCity", unique = true)
    String theatreCity;
}
