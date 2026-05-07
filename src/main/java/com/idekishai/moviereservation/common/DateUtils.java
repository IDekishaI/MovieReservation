package com.idekishai.moviereservation.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateUtils {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    public static LocalDate parseStringToLocalDate(String stringDate) {
        try {
            return LocalDate.parse(stringDate);
        } catch (DateTimeParseException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date format: " + stringDate);
        }
    }

    public static LocalTime parseStringToLocalTime(String stringTime) {
        try {
            return LocalTime.parse(stringTime);
        } catch (DateTimeParseException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid time format: " + stringTime);
        }
    }

    public static String formatDate(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }

    public static String formatTime(LocalTime time) {
        return time.format(TIME_FORMATTER);
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DATE_TIME_FORMATTER);
    }
}
