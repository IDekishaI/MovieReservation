package com.idekishai.moviereservation.auth.dtos;

public record UserPrincipal(
        String email,
        String name
) {
}
