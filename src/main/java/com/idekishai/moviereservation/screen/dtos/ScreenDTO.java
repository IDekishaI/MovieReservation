package com.idekishai.moviereservation.screen.dtos;

public record ScreenDTO(
        int screenId,
        int theatreId,
        String theatreName,
        String screenName,
        int totalSeats
) {
}
