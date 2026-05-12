package com.idekishai.moviereservation.movie;

import com.idekishai.moviereservation.movie.dtos.MovieRequestDTO;
import com.idekishai.moviereservation.movie.repositories.MovieRepository;
import com.idekishai.moviereservation.screen.dtos.ScreenRequestDTO;
import com.idekishai.moviereservation.screen.repositories.ScreenRepository;
import com.idekishai.moviereservation.showtime.dtos.ShowtimeRequestDTO;
import com.idekishai.moviereservation.showtime.repositories.ShowtimeRepository;
import com.idekishai.moviereservation.theatre.dtos.TheatreRequestDTO;
import com.idekishai.moviereservation.theatre.repositories.TheatreRepository;
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
    void saveMovie_shouldReturn209_whenDuplicateName() throws Exception {
        MovieRequestDTO dto1 = new MovieRequestDTO("Avatar", (short) 94, "Action");

        mockMvc.perform(post("/movies")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/movies")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto1)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Movie with the name \"Avatar\" already exists"));

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
    void getAllMovies_shouldReturn200_withPageOfMovies() throws Exception {
        MovieRequestDTO dto1 = new MovieRequestDTO("Avatar", (short) 94, "Action");
        MovieRequestDTO dto2 = new MovieRequestDTO("Conjuring", (short) 120, "Horror");

        mockMvc.perform(post("/movies")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/movies")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto2)))
                .andExpect(status().isCreated());


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
        MovieRequestDTO dto1 = new MovieRequestDTO("Avatar", (short) 94, "Action");

        String response = mockMvc.perform(post("/movies")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto1)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        int movieId = objectMapper.readTree(response).get("movieId").asInt();

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
        MovieRequestDTO dto1 = new MovieRequestDTO("Avatar", (short) 94, "Action");

        String response = mockMvc.perform(post("/movies")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto1)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        int movieId = objectMapper.readTree(response).get("movieId").asInt();

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
        MovieRequestDTO dto1 = new MovieRequestDTO("Avatar", (short) 94, "Action");

        mockMvc.perform(post("/movies")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto1)))
                .andExpect(status().isCreated());

        MovieRequestDTO dto2 = new MovieRequestDTO("Conjuring", (short) 120, "Horror");

        String response = mockMvc.perform(post("/movies")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto2)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        int movie2Id = objectMapper.readTree(response).get("movieId").asInt();

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
        MovieRequestDTO dto = new MovieRequestDTO("Avatar", (short) 94, "Action");

        String response = mockMvc.perform(post("/movies")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        int movieId = objectMapper.readTree(response).get("movieId").asInt();

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
    void deleteMovie_shouldReturn409_whenMovieHasShowtimes() throws Exception{
        MovieRequestDTO movieRequestDTO = new MovieRequestDTO("Avatar", (short) 94, "Action");

        String movieResponse = mockMvc.perform(post("/movies")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movieRequestDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        int movieId = objectMapper.readTree(movieResponse).get("movieId").asInt();

        TheatreRequestDTO theatreRequestDTO = new TheatreRequestDTO("Cineplexx", "Cara Konstantina 1", "Nis");

        String theatreResponse = mockMvc.perform(post("/theatres")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(theatreRequestDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        int theatreId = objectMapper.readTree(theatreResponse).get("theatreId").asInt();

        ScreenRequestDTO screenRequestDTO = new ScreenRequestDTO(theatreId, "IMAX 3D", (short)100);

        String screenResponse = mockMvc.perform(post("/screens")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(screenRequestDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        int screenId = objectMapper.readTree(screenResponse).get("screenId").asInt();

        ShowtimeRequestDTO showtimeRequestDTO = new ShowtimeRequestDTO(movieId, screenId, "2026-06-25", "11:00:00", 5F);

        mockMvc.perform(post("/showtimes")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(showtimeRequestDTO)))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/movies/" + movieId)
                .with(user("test@test.com").roles("ADMIN")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Movie with id " + movieId + " is being used in existing showtimes and cannot be deleted"));
    }
}
