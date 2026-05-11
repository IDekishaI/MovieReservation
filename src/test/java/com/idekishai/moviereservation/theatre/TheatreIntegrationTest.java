package com.idekishai.moviereservation.theatre;

import com.idekishai.moviereservation.screen.dtos.ScreenRequestDTO;
import com.idekishai.moviereservation.screen.repositories.ScreenRepository;
import com.idekishai.moviereservation.seat.repositories.SeatRepository;
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
public class TheatreIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TheatreRepository theatreRepository;

    @Autowired
    private ScreenRepository screenRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        seatRepository.deleteAll();
        screenRepository.deleteAll();
        theatreRepository.deleteAll();
    }

    @Test
    void saveTheatre_shouldReturn201_whenValidRequest() throws Exception {
        TheatreRequestDTO dto = new TheatreRequestDTO("Cineplexx", "Cara Konstantina 1", "Nis");

        mockMvc.perform(post("/theatre")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.theatreName").value("Cineplexx"))
                .andExpect(jsonPath("$.theatreAddress").value("Cara Konstantina 1"))
                .andExpect(jsonPath("$.theatreCity").value("Nis"));
    }

    @Test
    void saveTheatre_shouldReturn409_whenDuplicateNameAndCity() throws Exception {
        TheatreRequestDTO dto = new TheatreRequestDTO("Cineplexx", "Cara Konstantina 1", "Nis");

        mockMvc.perform(post("/theatre")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/theatre")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Theatre with name \"Cineplexx\" already exists in Nis"));
    }

    @Test
    void saveTheatre_shouldReturn400_whenBlankName() throws Exception {
        TheatreRequestDTO dto = new TheatreRequestDTO("", "Cara Konstantina 1", "Nis");

        mockMvc.perform(post("/theatre")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    void saveTheatre_shouldReturn403_whenNotAdmin() throws Exception {
        TheatreRequestDTO dto = new TheatreRequestDTO("Cineplexx", "Cara Konstantina 1", "Nis");

        mockMvc.perform(post("/theatre")
                        .with(user("test@test.com").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void saveTheatre_shouldReturn401_whenNotAuthenticated() throws Exception {
        TheatreRequestDTO dto = new TheatreRequestDTO("Cineplexx", "Cara Konstantina 1", "Nis");

        mockMvc.perform(post("/theatre")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateTheatre_shouldReturn200_whenValidRequest() throws Exception {
        TheatreRequestDTO createDto = new TheatreRequestDTO("Cineplexx", "Cara Konstantina 1", "Nis");

        String response = mockMvc.perform(post("/theatre")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        int theatreId = objectMapper.readTree(response).get("theatreId").asInt();

        TheatreRequestDTO updateDto = new TheatreRequestDTO("CineStar", "Nikole Tesle 2", "Novi Sad");

        mockMvc.perform(put("/theatre/" + theatreId)
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.theatreName").value("CineStar"))
                .andExpect(jsonPath("$.theatreCity").value("Novi Sad"));
    }

    @Test
    void updateTheatre_shouldReturn404_whenNotFound() throws Exception {
        TheatreRequestDTO dto = new TheatreRequestDTO("Cineplexx", "Cara Konstantina 1", "Nis");

        mockMvc.perform(put("/theatre/999")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Theatre with id 999 not found"));
    }

    @Test
    void deleteTheatre_shouldReturn200_whenExists() throws Exception {
        TheatreRequestDTO createDto = new TheatreRequestDTO("Cineplexx", "Cara Konstantina 1", "Nis");

        String response = mockMvc.perform(post("/theatre")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        int theatreId = objectMapper.readTree(response).get("theatreId").asInt();

        mockMvc.perform(delete("/theatre/" + theatreId)
                        .with(user("test@test.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().string("Theatre deleted successfully"));
    }

    @Test
    void deleteTheatre_shouldReturn404_whenNotFound() throws Exception {
        mockMvc.perform(delete("/theatre/999")
                        .with(user("test@test.com").roles("ADMIN")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Theatre with id 999 not found"));
    }

    @Test
    void getAllTheatres_shouldReturn200_withListOfTheatres() throws Exception {
        TheatreRequestDTO dto1 = new TheatreRequestDTO("Cineplexx", "Cara Konstantina 1", "Nis");
        TheatreRequestDTO dto2 = new TheatreRequestDTO("CineStar", "Nikole Tesle 2", "Novi Sad");

        mockMvc.perform(post("/theatre")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/theatre")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto2)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/theatres")
                        .with(user("test@test.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void deleteTheatre_shouldReturn409_whenTheatreHasScreens() throws Exception {
        TheatreRequestDTO createDto = new TheatreRequestDTO("Cineplexx", "Cara Konstantina 1", "Nis");

        String response = mockMvc.perform(post("/theatre")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        int theatreId = objectMapper.readTree(response).get("theatreId").asInt();

        ScreenRequestDTO screenDto = new ScreenRequestDTO(theatreId, "Screen 1", (short) 100);

        mockMvc.perform(post("/screens")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(screenDto)))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/theatre/" + theatreId)
                        .with(user("test@test.com").roles("ADMIN")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Theatre with id " + theatreId + " has existing screens and cannot be deleted"));
    }

    @Test
    void updateTheatre_shouldReturn200_whenKeepingSameNameAndCity() throws Exception {
        TheatreRequestDTO createDto = new TheatreRequestDTO("Cineplexx", "Cara Konstantina 1", "Nis");

        String response = mockMvc.perform(post("/theatre")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        int theatreId = objectMapper.readTree(response).get("theatreId").asInt();

        TheatreRequestDTO updateDto = new TheatreRequestDTO("Cineplexx", "Vojvode Misica 5", "Nis");

        mockMvc.perform(put("/theatre/" + theatreId)
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.theatreName").value("Cineplexx"))
                .andExpect(jsonPath("$.theatreAddress").value("Vojvode Misica 5"))
                .andExpect(jsonPath("$.theatreCity").value("Nis"));
    }

    @Test
    void updateTheatre_shouldReturn409_whenDuplicateNameAndCity() throws Exception {
        TheatreRequestDTO firstDto = new TheatreRequestDTO("Cineplexx", "Cara Konstantina 1", "Nis");

        mockMvc.perform(post("/theatre")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstDto)))
                .andExpect(status().isCreated());

        TheatreRequestDTO secondDto = new TheatreRequestDTO("CineStar", "Nikole Tesle 2", "Novi Sad");

        String response = mockMvc.perform(post("/theatre")
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        int secondTheatreId = objectMapper.readTree(response).get("theatreId").asInt();

        TheatreRequestDTO updateDto = new TheatreRequestDTO("Cineplexx", "Knjazevacka 3", "Nis");

        mockMvc.perform(put("/theatre/" + secondTheatreId)
                        .with(user("test@test.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Theatre with name \"Cineplexx\" already exists in Nis"));
    }
}
