package com.idekishai.moviereservation.screen;

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
public class ScreenIntegrationTest {
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
    void saveScreen_shouldReturn201_whenValidRequest() throws Exception{
        TheatreRequestDTO theatreRequestDTO = new TheatreRequestDTO("Cineplexx", "Cara Konstantina 1", "Nis");

        String theatreResponse = mockMvc.perform(post("/theatres")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(theatreRequestDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        int theatreId = objectMapper.readTree(theatreResponse).get("theatreId").asInt();

        ScreenRequestDTO screenRequestDTO = new ScreenRequestDTO(theatreId, "CINEMAX 3D", (short)85);

        mockMvc.perform(post("/screens")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(screenRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.screenName").value("CINEMAX 3D"))
                .andExpect(jsonPath("$.theatreId").value(theatreId))
                .andExpect(jsonPath("$.totalSeats").value(85));
    }

    @Test
    void saveScreen_shouldReturn209_whenDuplicateNameAndTheatreId() throws Exception{
        TheatreRequestDTO theatreRequestDTO = new TheatreRequestDTO("Cineplexx", "Cara Konstantina 1", "Nis");

        String theatreResponse = mockMvc.perform(post("/theatres")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(theatreRequestDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        int theatreId = objectMapper.readTree(theatreResponse).get("theatreId").asInt();

        ScreenRequestDTO screenRequestDTO = new ScreenRequestDTO(theatreId, "CINEMAX 3D", (short)85);

        mockMvc.perform(post("/screens")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(screenRequestDTO)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/screens")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(screenRequestDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Screen with name \"CINEMAX 3D\" already exists at theatre " + theatreId));
    }

    @Test
    void saveScreen_shouldReturn401_whenNotAuthenticated() throws Exception{
        TheatreRequestDTO theatreRequestDTO = new TheatreRequestDTO("Cineplexx", "Cara Konstantina 1", "Nis");

        String theatreResponse = mockMvc.perform(post("/theatres")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(theatreRequestDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        int theatreId = objectMapper.readTree(theatreResponse).get("theatreId").asInt();

        ScreenRequestDTO screenRequestDTO = new ScreenRequestDTO(theatreId, "CINEMAX 3D", (short)85);

        mockMvc.perform(post("/screens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(screenRequestDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void saveScreen_shouldReturn404_whenTheatreNotFound() throws Exception{
        ScreenRequestDTO screenRequestDTO = new ScreenRequestDTO(999, "CINEMAX 3D", (short)85);

        mockMvc.perform(post("/screens")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(screenRequestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Theatre with id 999 not found"));
    }

    @Test
    void getAllScreens_shouldReturn200_withPageOfScreens() throws Exception{
        TheatreRequestDTO theatreRequestDTO = new TheatreRequestDTO("Cineplexx", "Cara Konstantina 1", "Nis");

        String theatreResponse = mockMvc.perform(post("/theatres")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(theatreRequestDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        int theatreId = objectMapper.readTree(theatreResponse).get("theatreId").asInt();

        ScreenRequestDTO screenRequestDTO1 = new ScreenRequestDTO(theatreId, "CINEMAX 3D", (short)85);

        mockMvc.perform(post("/screens")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(screenRequestDTO1)))
                .andExpect(status().isCreated());

        ScreenRequestDTO screenRequestDTO2 = new ScreenRequestDTO(theatreId, "IMAX 3D", (short)50);

        mockMvc.perform(post("/screens")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(screenRequestDTO2)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/screens")
                        .with(user("test@test.com").roles("ADMIN"))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].screenName").value("CINEMAX 3D"))
                .andExpect(jsonPath("$.page.totalElements").value(2))
                .andExpect(jsonPath("$.page.totalPages").value(1));
    }

    @Test
    void updateScreen_shouldReturn200_whenValidRequest() throws Exception{
        TheatreRequestDTO theatreRequestDTO = new TheatreRequestDTO("Cineplexx", "Cara Konstantina 1", "Nis");

        String theatreResponse = mockMvc.perform(post("/theatres")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(theatreRequestDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        int theatreId = objectMapper.readTree(theatreResponse).get("theatreId").asInt();

        ScreenRequestDTO screenRequestDTO1 = new ScreenRequestDTO(theatreId, "CINEMAX 3D", (short)85);

        String screenResponse = mockMvc.perform(post("/screens")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(screenRequestDTO1)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        int screenId = objectMapper.readTree(screenResponse).get("screenId").asInt();

        ScreenRequestDTO screenRequestDTO2 = new ScreenRequestDTO(theatreId, "IMAX 3D", (short)50);

        mockMvc.perform(put("/screens/" + screenId)
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(screenRequestDTO2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.screenName").value("IMAX 3D"))
                .andExpect(jsonPath("$.theatreId").value(theatreId))
                .andExpect(jsonPath("$.totalSeats").value(50));
    }

    @Test
    void updateScreen_shouldReturn404_whenScreenNotFound() throws Exception{
        TheatreRequestDTO theatreRequestDTO = new TheatreRequestDTO("Cineplexx", "Cara Konstantina 1", "Nis");

        String theatreResponse = mockMvc.perform(post("/theatres")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(theatreRequestDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        int theatreId = objectMapper.readTree(theatreResponse).get("theatreId").asInt();

        ScreenRequestDTO screenRequestDTO = new ScreenRequestDTO(theatreId, "CINEMAX 3D", (short)85);

        mockMvc.perform(put("/screens/999")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(screenRequestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Screen with id 999 not found"));
    }

    @Test
    void updateScreen_shouldReturn200_whenKeepingSameNameAndTheatreId() throws Exception{
        TheatreRequestDTO theatreRequestDTO = new TheatreRequestDTO("Cineplexx", "Cara Konstantina 1", "Nis");

        String theatreResponse = mockMvc.perform(post("/theatres")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(theatreRequestDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        int theatreId = objectMapper.readTree(theatreResponse).get("theatreId").asInt();

        ScreenRequestDTO screenRequestDTO1 = new ScreenRequestDTO(theatreId, "CINEMAX 3D", (short)85);

        String screenResponse = mockMvc.perform(post("/screens")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(screenRequestDTO1)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        int screenId = objectMapper.readTree(screenResponse).get("screenId").asInt();

        ScreenRequestDTO screenRequestDTO2 = new ScreenRequestDTO(theatreId, "CINEMAX 3D", (short)70);

        mockMvc.perform(put("/screens/" + screenId)
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(screenRequestDTO2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.screenName").value("CINEMAX 3D"))
                .andExpect(jsonPath("$.theatreId").value(theatreId))
                .andExpect(jsonPath("$.totalSeats").value(70));
    }

    @Test
    void updateScreen_shouldReturn209_whenDuplicateNameAndTheatreId() throws Exception{
        TheatreRequestDTO theatreRequestDTO = new TheatreRequestDTO("Cineplexx", "Cara Konstantina 1", "Nis");

        String theatreResponse = mockMvc.perform(post("/theatres")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(theatreRequestDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        int theatreId = objectMapper.readTree(theatreResponse).get("theatreId").asInt();

        ScreenRequestDTO screenRequestDTO1 = new ScreenRequestDTO(theatreId, "CINEMAX 3D", (short)85);

        mockMvc.perform(post("/screens")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(screenRequestDTO1)))
                .andExpect(status().isCreated());

        ScreenRequestDTO screenRequestDTO2 = new ScreenRequestDTO(theatreId, "IMAX 3D", (short)70);

        String screen2Response = mockMvc.perform(post("/screens")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(screenRequestDTO2)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        int screenId = objectMapper.readTree(screen2Response).get("screenId").asInt();

        ScreenRequestDTO updateRequestDTO = new ScreenRequestDTO(theatreId, "CINEMAX 3D", (short)70);

        mockMvc.perform(put("/screens/" + screenId)
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Screen with name \"CINEMAX 3D\" already exists at theatre " + theatreId));
    }

    @Test
    void deleteScreen_shouldReturn200_whenExists() throws Exception{
        TheatreRequestDTO theatreRequestDTO = new TheatreRequestDTO("Cineplexx", "Cara Konstantina 1", "Nis");

        String theatreResponse = mockMvc.perform(post("/theatres")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(theatreRequestDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        int theatreId = objectMapper.readTree(theatreResponse).get("theatreId").asInt();

        ScreenRequestDTO screenRequestDTO = new ScreenRequestDTO(theatreId, "CINEMAX 3D", (short)85);

        String screenResponse = mockMvc.perform(post("/screens")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(screenRequestDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        int screenId = objectMapper.readTree(screenResponse).get("screenId").asInt();

        mockMvc.perform(delete("/screens/" + screenId)
                        .with(user("test@test.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().string("Screen deleted successfully"));
    }

    @Test
    void deleteScreen_shouldReturn404_whenNotFound() throws Exception{
        mockMvc.perform(delete("/screens/999")
                        .with(user("test@test.com").roles("ADMIN")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Screen with id 999 not found"));
    }

    @Test
    void deleteScreen_shouldReturn209_whenScreenHasShowtimes() throws Exception{
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

        ScreenRequestDTO screenRequestDTO = new ScreenRequestDTO(theatreId, "CINEMAX 3D", (short)85);

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

        mockMvc.perform(delete("/screens/" + screenId)
                        .with(user("test@test.com").roles("ADMIN")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Screen with id " + screenId + " is being used in showtimes and cannot be deleted"));
    }
}
