package com.idekishai.moviereservation.seat.seat_reservation.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record BookingRequestDTO(
        @Positive
        int seatReservationId,
        @NotBlank(message = "Card Holder Name cannot be blank")
        String cardHolderName,
        @NotBlank
        @Pattern(regexp = "^[0,9]{16}$", message = "Card number must be 16 digits")
        String cardNumber,
        @NotBlank
        @Pattern(regexp = "^(0[1-9]|1[0-2])/([0-9]{2})$", message = "Invalid expiry date format. Use MM/YY")
        String expiryDate,
        @NotBlank
        @Pattern(regexp = "^[0,9]{3,4}$", message = "CVV must be 3 or 4 digits")
        String cvv
) {
}
