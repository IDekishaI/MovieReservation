package com.idekishai.moviereservation.utils;

import com.idekishai.moviereservation.auth.dtos.UserPrincipal;
import com.idekishai.moviereservation.movie.dtos.MovieRequestDTO;
import com.idekishai.moviereservation.screen.dtos.ScreenRequestDTO;
import com.idekishai.moviereservation.seat.dtos.SeatRequestDTO;
import com.idekishai.moviereservation.seat.enums.SeatType;
import com.idekishai.moviereservation.seat.seat_reservation.dtos.BookingRequestDTO;
import com.idekishai.moviereservation.seat.seat_reservation.dtos.ReservationRequestDTO;
import com.idekishai.moviereservation.showtime.dtos.ShowtimeRequestDTO;
import com.idekishai.moviereservation.theatre.dtos.TheatreRequestDTO;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TestHelper {
    public static int createTheatre(MockMvc mockMvc, ObjectMapper objectMapper, String name, String address, String city) throws Exception {
        TheatreRequestDTO dto = new TheatreRequestDTO(name, address, city);
        String response = mockMvc.perform(post("/theatres")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("theatreId").asInt();
    }

    public static int createScreen(MockMvc mockMvc, ObjectMapper objectMapper, int theatreId, String name, short totalSeats) throws Exception {
        ScreenRequestDTO dto = new ScreenRequestDTO(theatreId, name, totalSeats);
        String response = mockMvc.perform(post("/screens")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("screenId").asInt();
    }

    public static int createMovie(MockMvc mockMvc, ObjectMapper objectMapper, String name, short length, String type) throws Exception {
        MovieRequestDTO dto = new MovieRequestDTO(name, length, type);
        String response = mockMvc.perform(post("/movies")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("movieId").asInt();
    }

    public static int createShowtime(MockMvc mockMvc, ObjectMapper objectMapper, int movieId, int screenId, String date, String time, float price) throws Exception {
        ShowtimeRequestDTO dto = new ShowtimeRequestDTO(movieId, screenId, date, time, price);
        String response = mockMvc.perform(post("/showtimes")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("showtimeId").asInt();
    }

    public static int createSeat(MockMvc mockMvc, ObjectMapper objectMapper, int screenId, String seatRow, short seatColumn, SeatType seatType, boolean inUse) throws Exception {
        SeatRequestDTO dto = new SeatRequestDTO(screenId, seatRow, seatColumn, seatType, inUse);
        String response = mockMvc.perform(post("/seats")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("seatId").asInt();
    }

    public static int lockSeat(MockMvc mockMvc, ObjectMapper objectMapper, int showtimeId, int seatId, String userEmail) throws Exception {
        ReservationRequestDTO dto = new ReservationRequestDTO(showtimeId, seatId);
        String response = mockMvc.perform(post("/reservations/lock")
                        .with(userPrincipal(userEmail))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("seatReservationId").asInt();
    }

    public static int bookSeat(MockMvc mockMvc, ObjectMapper objectMapper, int reservationId, String userEmail) throws Exception {
        BookingRequestDTO dto = new BookingRequestDTO(reservationId, "Marko Markovic", "1234567890123456", "12/27", "123");
        String response = mockMvc.perform(patch("/reservations/book")
                        .with(userPrincipal(userEmail))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("seatReservationId").asInt();
    }

    public static RequestPostProcessor userPrincipal(String email) {
        UserPrincipal principal = new UserPrincipal(email, "Test User");
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                principal, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        return SecurityMockMvcRequestPostProcessors.authentication(auth);
    }

    public static RequestPostProcessor adminPrincipal(String email) {
        UserPrincipal principal = new UserPrincipal(email, "Test Admin");
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                principal, null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        return SecurityMockMvcRequestPostProcessors.authentication(auth);
    }
}
