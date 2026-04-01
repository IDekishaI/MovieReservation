package com.idekishai.moviereservation.seat.seat_reservation.payment.repositories;

import com.idekishai.moviereservation.seat.seat_reservation.payment.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
}
