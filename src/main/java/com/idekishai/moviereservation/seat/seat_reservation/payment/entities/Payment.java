package com.idekishai.moviereservation.seat.seat_reservation.payment.entities;

import com.idekishai.moviereservation.seat.seat_reservation.entities.SeatReservation;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "payment")
public class Payment {
    @Id
    @Column(name = "paymentId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int paymentId;

    @OneToOne
    @JoinColumn(name = "seatReservationId")
    SeatReservation seatReservation;

    @Column(name = "cardHolderName", nullable = false)
    String cardHolderName;

    @Column(name = "lastFourDigits", nullable = false)
    String lastFourDigits;

    @Column(name = "expiryDate", nullable = false)
    String expiryDate;

    @Column(name = "paidAt", nullable = false)
    LocalDateTime paidAt;
}
