package com.idekishai.moviereservation.seat.seat_reservation.payment.repositories;

import com.idekishai.moviereservation.seat.seat_reservation.payment.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    Optional<Payment> findBySeatReservation_SeatReservationId(int seatReservationId);
}
