package com.idekishai.moviereservation.movie;

import com.idekishai.moviereservation.movie.dtos.MovieRequestDTO;
import com.idekishai.moviereservation.movie.repositories.MovieRepository;
import com.idekishai.moviereservation.screen.repositories.ScreenRepository;
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
public class MovieIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MovieRepository movieRepository;

    @Autowired
    ShowtimeRepository showtimeRepository;

    @Autowired
    ScreenRepository screenRepository;

    @Autowired
    TheatreRepository theatreRepository;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        showtimeRepository.deleteAll();
        screenRepository.deleteAll();
        theatreRepository.deleteAll();
        movieRepository.deleteAll();
    }

    @Test
    void saveMovie_shouldReturn201_whenValidRequest() throws Exception {
        MovieRequestDTO dto = new MovieRequestDTO("Avatar", (short) 94, "Action");

        mockMvc.perform(post("/movies")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.movieName").value("Avatar"))
                .andExpect(jsonPath("$.movieLength").value(94))
                .andExpect(jsonPath("$.movieType").value("Action"));
    }

    @Test
    void saveMovie_shouldReturn409_whenDuplicateName() throws Exception {
        TestHelper.createMovie(mockMvc, objectMapper, "Avatar", (short) 94, "Action");

        MovieRequestDTO dto1 = new MovieRequestDTO("Avatar", (short) 94, "Action");

        mockMvc.perform(post("/movies")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto1)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Movie with the name \"Avatar\" already exists"));

    }

    @Test
    void saveMovie_shouldReturn400_whenBlankName() throws Exception {
        MovieRequestDTO dto = new MovieRequestDTO("", (short) 94, "Action");

        mockMvc.perform(post("/movies")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    void saveMovie_shouldReturn400_whenInvalidMovieLength() throws Exception {
        MovieRequestDTO dto = new MovieRequestDTO("Avatar", (short) -1, "Action");

        mockMvc.perform(post("/movies")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    void saveMovie_shouldReturn401_whenNotAuthenticated() throws Exception {
        MovieRequestDTO dto1 = new MovieRequestDTO("Avatar", (short) 94, "Action");
        mockMvc.perform(post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto1)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void saveMovie_shouldReturn403_whenNotAdmin() throws Exception {
        MovieRequestDTO dto1 = new MovieRequestDTO("Avatar", (short) 94, "Action");

        mockMvc.perform(post("/movies")
                        .with(user("test@test.com").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto1)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllMovies_shouldReturn200_withPageOfMovies() throws Exception {
        TestHelper.createMovie(mockMvc, objectMapper, "Avatar", (short) 94, "Action");
        TestHelper.createMovie(mockMvc, objectMapper, "Conjuring", (short) 120, "Horror");

        mockMvc.perform(get("/movies")
                        .with(user("test@test.com").roles("USER"))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].movieName").value("Avatar"))
                .andExpect(jsonPath("$.page.totalElements").value(2))
                .andExpect(jsonPath("$.page.totalPages").value(1));
    }

    @Test
    void updateMovie_shouldReturn200_whenValidRequest() throws Exception {
        int movieId = TestHelper.createMovie(mockMvc, objectMapper, "Avatar", (short) 94, "Action");

        MovieRequestDTO dto2 = new MovieRequestDTO("Conjuring", (short) 120, "Horror");

        mockMvc.perform(put("/movies/" + movieId)
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.movieName").value("Conjuring"))
                .andExpect(jsonPath("$.movieLength").value(120))
                .andExpect(jsonPath("$.movieType").value("Horror"));
    }

    @Test
    void updateMovie_shouldReturn404_whenNotFound() throws Exception {
        MovieRequestDTO dto = new MovieRequestDTO("Avatar", (short) 94, "Action");

        mockMvc.perform(put("/movies/" + 999)
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Movie with id 999 not found"));
    }

    @Test
    void updateMovie_shouldReturn200_whenKeepingSameName() throws Exception {
        int movieId = TestHelper.createMovie(mockMvc, objectMapper, "Avatar", (short) 94, "Action");

        MovieRequestDTO dto2 = new MovieRequestDTO("Avatar", (short) 100, "Fiction");

        mockMvc.perform(put("/movies/" + movieId)
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.movieName").value("Avatar"))
                .andExpect(jsonPath("$.movieLength").value(100))
                .andExpect(jsonPath("$.movieType").value("Fiction"));
    }

    @Test
    void updateMovie_shouldReturn409_whenDuplicateName() throws Exception {
        TestHelper.createMovie(mockMvc, objectMapper, "Avatar", (short) 94, "Action");

        int movie2Id = TestHelper.createMovie(mockMvc, objectMapper, "Conjuring", (short) 120, "Horror");

        MovieRequestDTO updateDto = new MovieRequestDTO("Avatar", (short) 120, "Horror");

        mockMvc.perform(put("/movies/" + movie2Id)
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Movie with the name \"Avatar\" already exists"));
    }

    @Test
    void deleteMovie_shouldReturn200_whenExists() throws Exception {
        int movieId = TestHelper.createMovie(mockMvc, objectMapper, "Avatar", (short) 94, "Action");

        mockMvc.perform(delete("/movies/" + movieId)
                        .with(user("test@test.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().string("Movie deleted successfully"));
    }

    @Test
    void deleteMovie_shouldReturn404_whenNotFound() throws Exception {
        mockMvc.perform(delete("/movies/999")
                        .with(user("test@test.com").roles("ADMIN")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Movie with id 999 not found"));
    }

    @Test
    void deleteMovie_shouldReturn409_whenMovieHasShowtimes() throws Exception {
        int movieId = TestHelper.createMovie(mockMvc, objectMapper, "Avatar", (short) 94, "Action");

        int theatreId = TestHelper.createTheatre(mockMvc, objectMapper, "Cineplexx", "Cara Konstantina 1", "Nis");

        int screenId = TestHelper.createScreen(mockMvc, objectMapper, theatreId, "IMAX 3D", (short) 100);

        TestHelper.createShowtime(mockMvc, objectMapper, movieId, screenId, "2026-05-25", "11:00:00", 5F);

        mockMvc.perform(delete("/movies/" + movieId)
                        .with(user("test@test.com").roles("ADMIN")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Movie with id " + movieId + " is being used in existing showtimes and cannot be deleted"));
    }
}
