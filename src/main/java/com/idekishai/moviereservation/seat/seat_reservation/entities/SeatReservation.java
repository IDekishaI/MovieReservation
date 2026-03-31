package com.idekishai.moviereservation.seat.seat_reservation.entities;

import com.idekishai.moviereservation.seat.entities.Seat;
import com.idekishai.moviereservation.seat.enums.ReservationStatus;
import com.idekishai.moviereservation.showtime.entities.Showtime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "seat_reservation")
public class SeatReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seatReservationId", unique = true)
    int seatReservationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "showtimeId", nullable = false)
    Showtime showtime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seatId", nullable = false)
    Seat seat;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    ReservationStatus status;

    @Column(name = "lockedBy", nullable = false)
    String lockedBy;

    @Column(name = "lockedUntil")
    LocalDateTime lockedUntil;
}
