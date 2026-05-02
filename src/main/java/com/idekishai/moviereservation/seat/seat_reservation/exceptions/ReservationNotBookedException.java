package com.idekishai.moviereservation.seat.seat_reservation.exceptions;

public class ReservationNotBookedException extends RuntimeException {
    public ReservationNotBookedException(int searReservationId) {
        super("Seat reservation " + searReservationId + " has not been booked and has no payment to cancel");
    }
}
