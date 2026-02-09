package com.idekishai.moviereservation.seat.entities;

import com.idekishai.moviereservation.screen.entities.Screen;
import com.idekishai.moviereservation.seat.enums.SeatType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seatId", unique = true)
    int seatId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screenId", nullable = false)
    Screen screen;
    @Column(name = "seatRow", nullable = false)
    char seatRow;
    @Column(name = "seatColumn", nullable = false)
    short seatColumn;
    @Enumerated(EnumType.STRING)
    @Column(name = "seatType", nullable = false)
    SeatType seatType;
    @Column(name = "inUse", nullable = false)
    Boolean inUse;
}
