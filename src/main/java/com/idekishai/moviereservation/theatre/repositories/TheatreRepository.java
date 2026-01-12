package com.idekishai.moviereservation.theatre.repositories;

import com.idekishai.moviereservation.theatre.entities.Theatre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TheatreRepository extends JpaRepository<Theatre, Integer> {

}
