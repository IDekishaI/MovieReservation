package com.idekishai.moviereservation.seat.seat_reservation.payment.services;

import com.idekishai.moviereservation.common.SecurityUtils;
import com.idekishai.moviereservation.seat.seat_reservation.dtos.BookingRequestDTO;
import com.idekishai.moviereservation.seat.seat_reservation.entities.SeatReservation;
import com.idekishai.moviereservation.seat.seat_reservation.payment.entities.Payment;
import com.idekishai.moviereservation.seat.seat_reservation.payment.repositories.PaymentRepository;
import com.idekishai.moviereservation.seat.seat_reservation.repositories.SeatReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final SeatReservationRepository seatReservationRepository;

    public Payment savePayment(BookingRequestDTO dto) {
        Payment payment = new Payment();

        SeatReservation seatReservation = seatReservationRepository.findById(dto.seatReservationId()).orElseThrow(() -> new RuntimeException("Seat Reservation Id not found."));
        payment.setSeatReservation(seatReservation);
        payment.setCardHolderName(dto.cardHolderName().trim());

        String lastFourDigits = dto.cardNumber().substring(dto.cardNumber().length() - 4);
        payment.setLastFourDigits(lastFourDigits);

        payment.setExpiryDate(dto.expiryDate());
        payment.setPaidAt(LocalDateTime.now());

        Payment saved = paymentRepository.save(payment);

        log.info("Payment saved for reservation {} cardholder {}", seatReservation.getSeatReservationId(), payment.getCardHolderName());

        return saved;
    }

    public void deletePayment(int seatReservationId) {
        Payment payment = paymentRepository.findBySeatReservation_SeatReservationId(seatReservationId)
                .orElseThrow(() -> new RuntimeException("Payment for reservation " + seatReservationId + " not found"));

        paymentRepository.delete(payment);

        log.info("Payment deleted for reservation {} by {}", seatReservationId, SecurityUtils.getCurrentUserEmail());
    }
}
