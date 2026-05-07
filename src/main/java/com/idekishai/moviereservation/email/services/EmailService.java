package com.idekishai.moviereservation.email.services;

import com.idekishai.moviereservation.common.DateUtils;
import com.idekishai.moviereservation.email.dtos.BookingConfirmationEmailInfoDTO;
import com.idekishai.moviereservation.email.dtos.CancellationEmailInfoDTO;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendShowtimeCancellationEmail(CancellationEmailInfoDTO info) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setTo(info.email());
            helper.setSubject("Your booking for " + info.movieName() + " - Seat " + info.seatRow() + info.seatColumn() + " has been cancelled");
            helper.setText(
                    "<html><body>" +
                            "<p>Dear customer,</p>" +
                            "<p>Unfortunately your booking has been cancelled because the showtime was removed.</p>" +
                            "<p><strong>Booking details:</strong><br>" +
                            "Movie: " + info.movieName() + "<br>" +
                            "Theatre: " + info.theatreName() + ", " + info.theatreCity() + "<br>" +
                            "Screen: " + info.screenName() + "<br>" +
                            "Seat: " + info.seatRow() + info.seatColumn() + "<br>" +
                            "Date: " + DateUtils.formatDate(info.showtimeDate()) + "<br>" +
                            "Time: " + DateUtils.formatTime(info.showtimeTime()) + "</p>" +
                            "<p>We apologize for the inconvenience.</p>" +
                            "<p>Movie Reservation Team</p>" +
                            "</body></html>",
                    true
            );

            mailSender.send(mimeMessage);

            log.info("Showtime cancellation email sent to {}", info.email());
        } catch (Exception e) {
            log.error("Failed to send cancellation email to {}: {}", info.email(), e.getMessage());
        }
    }

    public void sendBookingConfirmationEmail(BookingConfirmationEmailInfoDTO info) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setTo(info.email());
            helper.setSubject("Booking Confirmed - " + info.movieName() + " - Seat " + info.seatRow() + info.seatColumn());
            helper.setText(
                    "<html><body>" +
                            "<p>Dear customer,</p>" +
                            "<p>Your booking has been confirmed!</p>" +
                            "<p><strong>Booking details:</strong><br>" +
                            "Movie: " + info.movieName() + "<br>" +
                            "Theatre: " + info.theatreName() + ", " + info.theatreCity() + "<br>" +
                            "Screen: " + info.screenName() + "<br>" +
                            "Seat: " + info.seatRow() + info.seatColumn() + "<br>" +
                            "Date: " + DateUtils.formatDate(info.showtimeDate()) + "<br>" +
                            "Time: " + DateUtils.formatTime(info.showtimeTime()) + "</p>" +
                            "<p><strong>Payment details:</strong><br>" +
                            "Cardholder: " + info.cardHolderName() + "<br>" +
                            "Card: **** **** **** " + info.lastFourDigits() + "<br>" +
                            "Paid at: " + DateUtils.formatDateTime(info.paidAt()) + "</p>" +
                            "<p>Enjoy the movie!</p>" +
                            "<p>Movie Reservation Team</p>" +
                            "</body></html>",
                    true
            );

            mailSender.send(mimeMessage);

            log.info("Booking confirmation email sent to {}", info.email());
        } catch (Exception e) {
            log.error("Failed to send booking confirmation email to {}: {}", info.email(), e.getMessage());
        }
    }

    public void sendBookingCancellationEmail(CancellationEmailInfoDTO info) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setTo(info.email());
            helper.setSubject("Booking Cancelled - " + info.movieName() + " - Seat " + info.seatRow() + info.seatColumn());
            helper.setText(
                    "<html><body>" +
                            "<p>Dear customer,</p>" +
                            "<p>Your booking has been successfully cancelled.</p>" +
                            "<p><strong>Booking details:</strong><br>" +
                            "Movie: " + info.movieName() + "<br>" +
                            "Theatre: " + info.theatreName() + ", " + info.theatreCity() + "<br>" +
                            "Screen: " + info.screenName() + "<br>" +
                            "Seat: " + info.seatRow() + info.seatColumn() + "<br>" +
                            "Date: " + DateUtils.formatDate(info.showtimeDate()) + "<br>" +
                            "Time: " + DateUtils.formatTime(info.showtimeTime()) + "</p>" +
                            "<p>If this was a mistake please rebook at your earliest convenience.</p>" +
                            "<p>Movie Reservation Team</p>" +
                            "</body></html>",
                    true
            );

            mailSender.send(mimeMessage);

            log.info("Booking cancellation email sent to {}", info.email());
        } catch (Exception e) {
            log.error("Failed to send booking cancellation email to {}: {}", info.email(), e.getMessage());
        }
    }
}
