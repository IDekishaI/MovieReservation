package com.idekishai.moviereservation.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class DateUtils {
    public static LocalDate parseStringToLocalDate(String stringDate){
        try {
            return LocalDate.parse(stringDate);
        } catch (DateTimeParseException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date format: " + stringDate);
        }
    }
    public static LocalTime parseStringToLocalTime(String stringTime){
        try {
            return LocalTime.parse(stringTime);
        } catch (DateTimeParseException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid time format: " + stringTime);
        }
    }
}
