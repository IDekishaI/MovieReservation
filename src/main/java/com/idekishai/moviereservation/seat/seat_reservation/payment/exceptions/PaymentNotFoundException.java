package com.idekishai.moviereservation.seat.seat_reservation.payment.exceptions;

public class PaymentNotFoundException extends RuntimeException {
    public PaymentNotFoundException(int seatReservationId) {
        super("Payment for seat reservation " + seatReservationId + " not found");
    }
}
