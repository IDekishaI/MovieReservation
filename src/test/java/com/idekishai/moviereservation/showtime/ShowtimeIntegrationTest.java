package com.idekishai.moviereservation.showtime;

import com.idekishai.moviereservation.movie.repositories.MovieRepository;
import com.idekishai.moviereservation.screen.repositories.ScreenRepository;
import com.idekishai.moviereservation.showtime.dtos.ShowtimeRequestDTO;
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

import java.time.LocalDate;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ShowtimeIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

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
        showtimeRepository.deleteAll();
        screenRepository.deleteAll();
        theatreRepository.deleteAll();
        movieRepository.deleteAll();
    }

    @Test
    void saveShowtime_shouldReturn201_whenValidRequest() throws Exception {
        int movieId = TestHelper.createMovie(mockMvc, objectMapper, "Avatar", (short) 94, "Action");
        int theatreId = TestHelper.createTheatre(mockMvc, objectMapper, "Cineplexx", "Cara Konstantina 1", "Nis");
        int screenId = TestHelper.createScreen(mockMvc, objectMapper, theatreId, "IMAX 3D", (short) 100);

        String futureDate = LocalDate.now().plusDays(1).toString();

        ShowtimeRequestDTO dto = new ShowtimeRequestDTO(movieId, screenId, futureDate, "11:00:00", 5F);

        mockMvc.perform(post("/showtimes")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.movieId").value(movieId))
                .andExpect(jsonPath("$.screenName").value("IMAX 3D"))
                .andExpect(jsonPath("$.price").value(5.0));
    }

    @Test
    void saveShowtime_shouldReturn404_whenMovieNotFound() throws Exception {
        int theatreId = TestHelper.createTheatre(mockMvc, objectMapper, "Cineplexx", "Cara Konstantina 1", "Nis");
        int screenId = TestHelper.createScreen(mockMvc, objectMapper, theatreId, "IMAX 3D", (short) 100);

        String futureDate = LocalDate.now().plusDays(1).toString();

        ShowtimeRequestDTO dto = new ShowtimeRequestDTO(999, screenId, futureDate, "11:00:00", 5F);

        mockMvc.perform(post("/showtimes")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Movie with id 999 not found"));
    }

    @Test
    void saveShowtime_shouldReturn404_whenScreenNotFound() throws Exception {
        int movieId = TestHelper.createMovie(mockMvc, objectMapper, "Avatar", (short) 94, "Action");

        String futureDate = LocalDate.now().plusDays(1).toString();

        ShowtimeRequestDTO dto = new ShowtimeRequestDTO(movieId, 999, futureDate, "11:00:00", 5F);

        mockMvc.perform(post("/showtimes")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Screen with id 999 not found"));
    }

    @Test
    void saveShowtime_shouldReturn400_whenInvalidDateFormat() throws Exception {
        int movieId = TestHelper.createMovie(mockMvc, objectMapper, "Avatar", (short) 94, "Action");
        int theatreId = TestHelper.createTheatre(mockMvc, objectMapper, "Cineplexx", "Cara Konstantina 1", "Nis");
        int screenId = TestHelper.createScreen(mockMvc, objectMapper, theatreId, "IMAX 3D", (short) 100);

        ShowtimeRequestDTO dto = new ShowtimeRequestDTO(movieId, screenId, "25-05-2026", "11:00:00", 5F);

        mockMvc.perform(post("/showtimes")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    void saveShowtime_shouldReturn400_whenInvalidTimeFormat() throws Exception {
        int movieId = TestHelper.createMovie(mockMvc, objectMapper, "Avatar", (short) 94, "Action");
        int theatreId = TestHelper.createTheatre(mockMvc, objectMapper, "Cineplexx", "Cara Konstantina 1", "Nis");
        int screenId = TestHelper.createScreen(mockMvc, objectMapper, theatreId, "IMAX 3D", (short) 100);

        String futureDate = LocalDate.now().plusDays(1).toString();

        ShowtimeRequestDTO dto = new ShowtimeRequestDTO(movieId, screenId, futureDate, "11:00", 5F);

        mockMvc.perform(post("/showtimes")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    void saveShowtime_shouldReturn400_whenNullPrice() throws Exception {
        int movieId = TestHelper.createMovie(mockMvc, objectMapper, "Avatar", (short) 94, "Action");
        int theatreId = TestHelper.createTheatre(mockMvc, objectMapper, "Cineplexx", "Cara Konstantina 1", "Nis");
        int screenId = TestHelper.createScreen(mockMvc, objectMapper, theatreId, "IMAX 3D", (short) 100);

        String futureDate = LocalDate.now().plusDays(1).toString();

        ShowtimeRequestDTO dto = new ShowtimeRequestDTO(movieId, screenId, futureDate, "11:00:00", null);

        mockMvc.perform(post("/showtimes")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    void saveShowtime_shouldReturn400_whenMovieIdNotPositive() throws Exception {
        int theatreId = TestHelper.createTheatre(mockMvc, objectMapper, "Cineplexx", "Cara Konstantina 1", "Nis");
        int screenId = TestHelper.createScreen(mockMvc, objectMapper, theatreId, "IMAX 3D", (short) 100);

        String futureDate = LocalDate.now().plusDays(1).toString();

        ShowtimeRequestDTO dto = new ShowtimeRequestDTO(-1, screenId, futureDate, "11:00:00", 5F);

        mockMvc.perform(post("/showtimes")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    void saveShowtime_shouldReturn409_whenOverlapsPastShowtime() throws Exception {
        int movieId = TestHelper.createMovie(mockMvc, objectMapper, "Avatar", (short) 94, "Action");
        int theatreId = TestHelper.createTheatre(mockMvc, objectMapper, "Cineplexx", "Cara Konstantina 1", "Nis");
        int screenId = TestHelper.createScreen(mockMvc, objectMapper, theatreId, "IMAX 3D", (short) 100);

        String futureDate = LocalDate.now().plusDays(1).toString();

        TestHelper.createShowtime(mockMvc, objectMapper, movieId, screenId, futureDate, "11:00:00", 5F);

        ShowtimeRequestDTO dto = new ShowtimeRequestDTO(movieId, screenId, futureDate, "12:00:00", 5F);

        mockMvc.perform(post("/showtimes")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Screen with id " + screenId + " is occupied during that time"));
    }

    @Test
    void saveShowtime_shouldReturn409_whenOverlapsFutureShowtime() throws Exception {
        int movieId = TestHelper.createMovie(mockMvc, objectMapper, "Avatar", (short) 94, "Action");
        int theatreId = TestHelper.createTheatre(mockMvc, objectMapper, "Cineplexx", "Cara Konstantina 1", "Nis");
        int screenId = TestHelper.createScreen(mockMvc, objectMapper, theatreId, "IMAX 3D", (short) 100);

        String futureDate = LocalDate.now().plusDays(1).toString();

        TestHelper.createShowtime(mockMvc, objectMapper, movieId, screenId, futureDate, "13:00:00", 5F);

        ShowtimeRequestDTO dto = new ShowtimeRequestDTO(movieId, screenId, futureDate, "12:00:00", 5F);

        mockMvc.perform(post("/showtimes")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Screen with id " + screenId + " is occupied during that time"));
    }

    @Test
    void saveShowtime_shouldReturn401_whenNotAuthenticated() throws Exception {
        int movieId = TestHelper.createMovie(mockMvc, objectMapper, "Avatar", (short) 94, "Action");
        int theatreId = TestHelper.createTheatre(mockMvc, objectMapper, "Cineplexx", "Cara Konstantina 1", "Nis");
        int screenId = TestHelper.createScreen(mockMvc, objectMapper, theatreId, "IMAX 3D", (short) 100);

        String futureDate = LocalDate.now().plusDays(1).toString();

        ShowtimeRequestDTO dto = new ShowtimeRequestDTO(movieId, screenId, futureDate, "11:00:00", 5F);

        mockMvc.perform(post("/showtimes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void saveShowtime_shouldReturn403_whenNotAdmin() throws Exception {
        int movieId = TestHelper.createMovie(mockMvc, objectMapper, "Avatar", (short) 94, "Action");
        int theatreId = TestHelper.createTheatre(mockMvc, objectMapper, "Cineplexx", "Cara Konstantina 1", "Nis");
        int screenId = TestHelper.createScreen(mockMvc, objectMapper, theatreId, "IMAX 3D", (short) 100);

        String futureDate = LocalDate.now().plusDays(1).toString();

        ShowtimeRequestDTO dto = new ShowtimeRequestDTO(movieId, screenId, futureDate, "11:00:00", 5F);

        mockMvc.perform(post("/showtimes")
                        .with(user("test@test.com").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getShowtimesByTheatre_shouldReturn200_withListOfShowtimes() throws Exception {
        int movieId = TestHelper.createMovie(mockMvc, objectMapper, "Avatar", (short) 94, "Action");
        int theatreId = TestHelper.createTheatre(mockMvc, objectMapper, "Cineplexx", "Cara Konstantina 1", "Nis");
        int screenId = TestHelper.createScreen(mockMvc, objectMapper, theatreId, "IMAX 3D", (short) 100);

        String futureDate = LocalDate.now().plusDays(1).toString();

        TestHelper.createShowtime(mockMvc, objectMapper, movieId, screenId, futureDate, "11:00:00", 5F);
        TestHelper.createShowtime(mockMvc, objectMapper, movieId, screenId, futureDate, "15:00:00", 5F);

        mockMvc.perform(get("/showtimes/theatre/" + theatreId)
                        .with(user("test@test.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].theatreId").value(theatreId));
    }

    @Test
    void getShowtimesByMovie_shouldReturn200_withListOfShowtimes() throws Exception {
        int movieId = TestHelper.createMovie(mockMvc, objectMapper, "Avatar", (short) 94, "Action");
        int theatreId = TestHelper.createTheatre(mockMvc, objectMapper, "Cineplexx", "Cara Konstantina 1", "Nis");
        int screenId = TestHelper.createScreen(mockMvc, objectMapper, theatreId, "IMAX 3D", (short) 100);

        String futureDate = LocalDate.now().plusDays(1).toString();

        TestHelper.createShowtime(mockMvc, objectMapper, movieId, screenId, futureDate, "11:00:00", 5F);
        TestHelper.createShowtime(mockMvc, objectMapper, movieId, screenId, futureDate, "15:00:00", 5F);

        mockMvc.perform(get("/showtimes/movie/" + movieId)
                        .with(user("test@test.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].movieId").value(movieId));
    }

    @Test
    void updateShowtime_shouldReturn200_whenValidRequest() throws Exception {
        int movieId = TestHelper.createMovie(mockMvc, objectMapper, "Avatar", (short) 94, "Action");
        int theatreId = TestHelper.createTheatre(mockMvc, objectMapper, "Cineplexx", "Cara Konstantina 1", "Nis");
        int screenId = TestHelper.createScreen(mockMvc, objectMapper, theatreId, "IMAX 3D", (short) 100);

        String futureDate = LocalDate.now().plusDays(1).toString();

        int showtimeId = TestHelper.createShowtime(mockMvc, objectMapper, movieId, screenId, futureDate, "11:00:00", 5F);

        ShowtimeRequestDTO updateDto = new ShowtimeRequestDTO(movieId, screenId, futureDate, "15:00:00", 8F);

        mockMvc.perform(put("/showtimes/" + showtimeId)
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.showtimeTime").value("15:00:00"))
                .andExpect(jsonPath("$.price").value(8.0));
    }

    @Test
    void updateShowtime_shouldReturn404_whenNotFound() throws Exception {
        int movieId = TestHelper.createMovie(mockMvc, objectMapper, "Avatar", (short) 94, "Action");
        int theatreId = TestHelper.createTheatre(mockMvc, objectMapper, "Cineplexx", "Cara Konstantina 1", "Nis");
        int screenId = TestHelper.createScreen(mockMvc, objectMapper, theatreId, "IMAX 3D", (short) 100);

        String futureDate = LocalDate.now().plusDays(1).toString();

        ShowtimeRequestDTO dto = new ShowtimeRequestDTO(movieId, screenId, futureDate, "11:00:00", 5F);

        mockMvc.perform(put("/showtimes/999")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Showtime with id 999 not found"));
    }

    @Test
    void updateShowtime_shouldReturn409_whenOverlaps() throws Exception {
        int movieId = TestHelper.createMovie(mockMvc, objectMapper, "Avatar", (short) 94, "Action");
        int theatreId = TestHelper.createTheatre(mockMvc, objectMapper, "Cineplexx", "Cara Konstantina 1", "Nis");
        int screenId = TestHelper.createScreen(mockMvc, objectMapper, theatreId, "IMAX 3D", (short) 100);

        String futureDate = LocalDate.now().plusDays(1).toString();

        TestHelper.createShowtime(mockMvc, objectMapper, movieId, screenId, futureDate, "11:00:00", 5F);

        int showtimeId = TestHelper.createShowtime(mockMvc, objectMapper, movieId, screenId, futureDate, "15:00:00", 5F);

        ShowtimeRequestDTO updateDto = new ShowtimeRequestDTO(movieId, screenId, futureDate, "12:00:00", 5F);

        mockMvc.perform(put("/showtimes/" + showtimeId)
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Screen with id " + screenId + " is occupied during that time"));
    }

    @Test
    void deleteShowtime_shouldReturn200_whenExists() throws Exception {
        int movieId = TestHelper.createMovie(mockMvc, objectMapper, "Avatar", (short) 94, "Action");
        int theatreId = TestHelper.createTheatre(mockMvc, objectMapper, "Cineplexx", "Cara Konstantina 1", "Nis");
        int screenId = TestHelper.createScreen(mockMvc, objectMapper, theatreId, "IMAX 3D", (short) 100);

        String futureDate = LocalDate.now().plusDays(1).toString();

        int showtimeId = TestHelper.createShowtime(mockMvc, objectMapper, movieId, screenId, futureDate, "11:00:00", 5F);

        mockMvc.perform(delete("/showtimes/" + showtimeId)
                        .with(user("test@test.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().string("Showtime deleted successfully"));
    }

    @Test
    void deleteShowtime_shouldReturn404_whenNotFound() throws Exception {
        mockMvc.perform(delete("/showtimes/999")
                        .with(user("test@test.com").roles("ADMIN")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Showtime with id 999 not found"));
    }
}