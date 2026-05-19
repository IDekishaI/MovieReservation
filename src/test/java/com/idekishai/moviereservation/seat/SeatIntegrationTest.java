package com.idekishai.moviereservation.seat;

import com.idekishai.moviereservation.movie.repositories.MovieRepository;
import com.idekishai.moviereservation.screen.repositories.ScreenRepository;
import com.idekishai.moviereservation.seat.dtos.SeatRequestDTO;
import com.idekishai.moviereservation.seat.enums.SeatType;
import com.idekishai.moviereservation.seat.repositories.SeatRepository;
import com.idekishai.moviereservation.seat.seat_reservation.repositories.SeatReservationRepository;
import com.idekishai.moviereservation.showtime.repositories.ShowtimeRepository;
import com.idekishai.moviereservation.theatre.repositories.TheatreRepository;
import com.idekishai.moviereservation.utils.TestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SeatIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    SeatRepository seatRepository;

    @Autowired
    SeatReservationRepository seatReservationRepository;

    @Autowired
    ShowtimeRepository showtimeRepository;

    @Autowired
    ScreenRepository screenRepository;

    @Autowired
    TheatreRepository theatreRepository;

    @Autowired
    MovieRepository movieRepository;

    @BeforeEach
    void setUp() {
        seatReservationRepository.deleteAll();
        seatRepository.deleteAll();
        showtimeRepository.deleteAll();
        screenRepository.deleteAll();
        theatreRepository.deleteAll();
        movieRepository.deleteAll();
    }

    @Test
    void saveSeat_shouldReturn201_whenValidRequest() throws Exception {
        int theatreId = TestHelper.createTheatre(mockMvc, objectMapper, "Cineplexx", "Cara Konstantina 1", "Nis");
        int screenId = TestHelper.createScreen(mockMvc, objectMapper, theatreId, "IMAX 3D", (short) 100);

        SeatRequestDTO dto = new SeatRequestDTO(screenId, "A", (short) 1, SeatType.REGULAR, true);

        mockMvc.perform(post("/seats")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.seatRow").value("A"))
                .andExpect(jsonPath("$.seatColumn").value(1))
                .andExpect(jsonPath("$.seatType").value("REGULAR"))
                .andExpect(jsonPath("$.screenId").value(screenId));
    }

    @Test
    void saveSeat_shouldReturn404_whenScreenNotFound() throws Exception {
        SeatRequestDTO dto = new SeatRequestDTO(999, "A", (short) 1, SeatType.REGULAR, true);

        mockMvc.perform(post("/seats")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Screen with id 999 not found"));
    }

    @Test
    void saveSeat_shouldReturn409_whenDuplicateRowAndColumn() throws Exception {
        int theatreId = TestHelper.createTheatre(mockMvc, objectMapper, "Cineplexx", "Cara Konstantina 1", "Nis");
        int screenId = TestHelper.createScreen(mockMvc, objectMapper, theatreId, "IMAX 3D", (short) 100);

        TestHelper.createSeat(mockMvc, objectMapper, screenId, "A", (short) 1, SeatType.REGULAR, true);

        SeatRequestDTO dto = new SeatRequestDTO(screenId, "A", (short) 1, SeatType.REGULAR, true);

        mockMvc.perform(post("/seats")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Seat already exists at row A column 1"));
    }

    @Test
    void saveSeat_shouldReturn400_whenInvalidSeatRow() throws Exception {
        int theatreId = TestHelper.createTheatre(mockMvc, objectMapper, "Cineplexx", "Cara Konstantina 1", "Nis");
        int screenId = TestHelper.createScreen(mockMvc, objectMapper, theatreId, "IMAX 3D", (short) 100);

        SeatRequestDTO dto = new SeatRequestDTO(screenId, "a", (short) 1, SeatType.REGULAR, true);

        mockMvc.perform(post("/seats")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    void saveSeat_shouldReturn400_whenColumnOutOfRange() throws Exception {
        int theatreId = TestHelper.createTheatre(mockMvc, objectMapper, "Cineplexx", "Cara Konstantina 1", "Nis");
        int screenId = TestHelper.createScreen(mockMvc, objectMapper, theatreId, "IMAX 3D", (short) 100);

        SeatRequestDTO dto = new SeatRequestDTO(screenId, "A", (short) 51, SeatType.REGULAR, true);

        mockMvc.perform(post("/seats")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    void saveSeat_shouldReturn401_whenNotAuthenticated() throws Exception {
        int theatreId = TestHelper.createTheatre(mockMvc, objectMapper, "Cineplexx", "Cara Konstantina 1", "Nis");
        int screenId = TestHelper.createScreen(mockMvc, objectMapper, theatreId, "IMAX 3D", (short) 100);

        SeatRequestDTO dto = new SeatRequestDTO(screenId, "A", (short) 1, SeatType.REGULAR, true);

        mockMvc.perform(post("/seats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void saveSeat_shouldReturn403_whenNotAdmin() throws Exception {
        int theatreId = TestHelper.createTheatre(mockMvc, objectMapper, "Cineplexx", "Cara Konstantina 1", "Nis");
        int screenId = TestHelper.createScreen(mockMvc, objectMapper, theatreId, "IMAX 3D", (short) 100);

        SeatRequestDTO dto = new SeatRequestDTO(screenId, "A", (short) 1, SeatType.REGULAR, true);

        mockMvc.perform(post("/seats")
                        .with(user("test@test.com").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllSeats_shouldReturn200_withPageOfSeats() throws Exception {
        int theatreId = TestHelper.createTheatre(mockMvc, objectMapper, "Cineplexx", "Cara Konstantina 1", "Nis");
        int screenId = TestHelper.createScreen(mockMvc, objectMapper, theatreId, "IMAX 3D", (short) 100);

        TestHelper.createSeat(mockMvc, objectMapper, screenId, "A", (short) 1, SeatType.REGULAR, true);
        TestHelper.createSeat(mockMvc, objectMapper, screenId, "A", (short) 2, SeatType.PREMIUM, true);

        mockMvc.perform(get("/seats")
                        .with(user("test@test.com").roles("ADMIN"))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.page.totalElements").value(2));
    }

    @Test
    void getSeatsByScreen_shouldReturn200_withListOfSeats() throws Exception {
        int theatreId = TestHelper.createTheatre(mockMvc, objectMapper, "Cineplexx", "Cara Konstantina 1", "Nis");
        int screenId = TestHelper.createScreen(mockMvc, objectMapper, theatreId, "IMAX 3D", (short) 100);

        TestHelper.createSeat(mockMvc, objectMapper, screenId, "A", (short) 1, SeatType.REGULAR, true);
        TestHelper.createSeat(mockMvc, objectMapper, screenId, "A", (short) 2, SeatType.PREMIUM, true);

        mockMvc.perform(get("/seats/" + screenId)
                        .with(user("test@test.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].screenId").value(screenId));
    }

    @Test
    void updateSeat_shouldReturn200_whenValidRequest() throws Exception {
        int theatreId = TestHelper.createTheatre(mockMvc, objectMapper, "Cineplexx", "Cara Konstantina 1", "Nis");
        int screenId = TestHelper.createScreen(mockMvc, objectMapper, theatreId, "IMAX 3D", (short) 100);
        int seatId = TestHelper.createSeat(mockMvc, objectMapper, screenId, "A", (short) 1, SeatType.REGULAR, true);

        SeatRequestDTO updateDto = new SeatRequestDTO(screenId, "B", (short) 2, SeatType.PREMIUM, true);

        mockMvc.perform(put("/seats/" + seatId)
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seatRow").value("B"))
                .andExpect(jsonPath("$.seatColumn").value(2))
                .andExpect(jsonPath("$.seatType").value("PREMIUM"));
    }

    @Test
    void updateSeat_shouldReturn404_whenNotFound() throws Exception {
        int theatreId = TestHelper.createTheatre(mockMvc, objectMapper, "Cineplexx", "Cara Konstantina 1", "Nis");
        int screenId = TestHelper.createScreen(mockMvc, objectMapper, theatreId, "IMAX 3D", (short) 100);

        SeatRequestDTO dto = new SeatRequestDTO(screenId, "A", (short) 1, SeatType.REGULAR, true);

        mockMvc.perform(put("/seats/999")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Seat with id 999 not found"));
    }

    @Test
    void updateSeat_shouldReturn200_whenKeepingSameRowAndColumn() throws Exception {
        int theatreId = TestHelper.createTheatre(mockMvc, objectMapper, "Cineplexx", "Cara Konstantina 1", "Nis");
        int screenId = TestHelper.createScreen(mockMvc, objectMapper, theatreId, "IMAX 3D", (short) 100);
        int seatId = TestHelper.createSeat(mockMvc, objectMapper, screenId, "A", (short) 1, SeatType.REGULAR, true);

        SeatRequestDTO updateDto = new SeatRequestDTO(screenId, "A", (short) 1, SeatType.PREMIUM, true);

        mockMvc.perform(put("/seats/" + seatId)
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seatRow").value("A"))
                .andExpect(jsonPath("$.seatColumn").value(1))
                .andExpect(jsonPath("$.seatType").value("PREMIUM"));
    }

    @Test
    void updateSeat_shouldReturn409_whenDuplicateRowAndColumn() throws Exception {
        int theatreId = TestHelper.createTheatre(mockMvc, objectMapper, "Cineplexx", "Cara Konstantina 1", "Nis");
        int screenId = TestHelper.createScreen(mockMvc, objectMapper, theatreId, "IMAX 3D", (short) 100);

        TestHelper.createSeat(mockMvc, objectMapper, screenId, "A", (short) 1, SeatType.REGULAR, true);
        int seatId = TestHelper.createSeat(mockMvc, objectMapper, screenId, "B", (short) 2, SeatType.REGULAR, true);

        SeatRequestDTO updateDto = new SeatRequestDTO(screenId, "A", (short) 1, SeatType.REGULAR, true);

        mockMvc.perform(put("/seats/" + seatId)
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Seat already exists at row A column 1"));
    }

    @Test
    void updateSeat_shouldReturn409_whenSeatInUse() throws Exception {
        int movieId = TestHelper.createMovie(mockMvc, objectMapper, "Avatar", (short) 94, "Action");
        int theatreId = TestHelper.createTheatre(mockMvc, objectMapper, "Cineplexx", "Cara Konstantina 1", "Nis");
        int screenId = TestHelper.createScreen(mockMvc, objectMapper, theatreId, "IMAX 3D", (short) 100);
        int seatId = TestHelper.createSeat(mockMvc, objectMapper, screenId, "A", (short) 1, SeatType.REGULAR, true);

        String futureDate = java.time.LocalDate.now().plusDays(1).toString();
        int showtimeId = TestHelper.createShowtime(mockMvc, objectMapper, movieId, screenId, futureDate, "11:00:00", 5F);

        TestHelper.lockSeat(mockMvc, objectMapper, showtimeId, seatId, "test@test.com");

        SeatRequestDTO updateDto = new SeatRequestDTO(screenId, "B", (short) 2, SeatType.PREMIUM, true);

        mockMvc.perform(put("/seats/" + seatId)
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Seat with id " + seatId + " is being used in seat reservations and cannot be changed"));
    }

    @Test
    void deleteSeat_shouldReturn200_whenExists() throws Exception {
        int theatreId = TestHelper.createTheatre(mockMvc, objectMapper, "Cineplexx", "Cara Konstantina 1", "Nis");
        int screenId = TestHelper.createScreen(mockMvc, objectMapper, theatreId, "IMAX 3D", (short) 100);
        int seatId = TestHelper.createSeat(mockMvc, objectMapper, screenId, "A", (short) 1, SeatType.REGULAR, true);

        mockMvc.perform(delete("/seats/" + seatId)
                        .with(user("test@test.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().string("Seat deleted successfully"));
    }

    @Test
    void deleteSeat_shouldReturn404_whenNotFound() throws Exception {
        mockMvc.perform(delete("/seats/999")
                        .with(user("test@test.com").roles("ADMIN")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Seat with id 999 not found"));
    }

    @Test
    void deleteSeat_shouldReturn409_whenSeatInUse() throws Exception {
        int movieId = TestHelper.createMovie(mockMvc, objectMapper, "Avatar", (short) 94, "Action");
        int theatreId = TestHelper.createTheatre(mockMvc, objectMapper, "Cineplexx", "Cara Konstantina 1", "Nis");
        int screenId = TestHelper.createScreen(mockMvc, objectMapper, theatreId, "IMAX 3D", (short) 100);
        int seatId = TestHelper.createSeat(mockMvc, objectMapper, screenId, "A", (short) 1, SeatType.REGULAR, true);

        String futureDate = java.time.LocalDate.now().plusDays(1).toString();
        int showtimeId = TestHelper.createShowtime(mockMvc, objectMapper, movieId, screenId, futureDate, "11:00:00", 5F);

        TestHelper.lockSeat(mockMvc, objectMapper, showtimeId, seatId, "test@test.com");

        mockMvc.perform(delete("/seats/" + seatId)
                        .with(user("test@test.com").roles("ADMIN")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Seat with id " + seatId + " is being used in seat reservations and cannot be changed"));
    }
}