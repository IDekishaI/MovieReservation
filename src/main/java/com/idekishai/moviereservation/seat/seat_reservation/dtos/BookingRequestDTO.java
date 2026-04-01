package com.idekishai.moviereservation.seat.seat_reservation.dtos;

public record BookingRequestDTO(
        int seatReservationId,
        String cardHolderName,
        String cardNumber,
        String expiryDate,
        String cvv
) {
}
