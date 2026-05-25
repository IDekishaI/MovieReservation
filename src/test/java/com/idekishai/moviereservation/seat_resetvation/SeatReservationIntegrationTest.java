package com.idekishai.moviereservation.seat_resetvation;

import com.idekishai.moviereservation.auth.dtos.UserPrincipal;
import com.idekishai.moviereservation.email.services.EmailService;
import com.idekishai.moviereservation.movie.repositories.MovieRepository;
import com.idekishai.moviereservation.ratelimiter.services.RateLimiterService;
import com.idekishai.moviereservation.screen.repositories.ScreenRepository;
import com.idekishai.moviereservation.seat.enums.SeatType;
import com.idekishai.moviereservation.seat.repositories.SeatRepository;
import com.idekishai.moviereservation.seat.seat_reservation.dtos.BookingRequestDTO;
import com.idekishai.moviereservation.seat.seat_reservation.dtos.ReservationRequestDTO;
import com.idekishai.moviereservation.seat.seat_reservation.entities.SeatReservation;
import com.idekishai.moviereservation.seat.seat_reservation.payment.repositories.PaymentRepository;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SeatReservationIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PaymentRepository paymentRepository;

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

    @MockitoBean
    RateLimiterService rateLimiterService;

    @MockitoBean
    EmailService emailService;

    @BeforeEach
    void setUp() {
        paymentRepository.deleteAll();
        seatReservationRepository.deleteAll();
        seatRepository.deleteAll();
        showtimeRepository.deleteAll();
        screenRepository.deleteAll();
        theatreRepository.deleteAll();
        movieRepository.deleteAll();
    }

    @Test
    void lockSeat_shouldReturn201_whenValidRequest() throws Exception {
        int movieId = TestHelper.createMovie(mockMvc, objectMapper, "Avatar", (short) 94, "Action");
        int theatreId = TestHelper.createTheatre(mockMvc, objectMapper, "Cineplexx", "Cara Konstantina 1", "Nis");
        int screenId = TestHelper.createScreen(mockMvc, objectMapper, theatreId, "IMAX 3D", (short) 100);
        String futureDate = java.time.LocalDate.now().plusDays(1).toString();
        int showtimeId = TestHelper.createShowtime(mockMvc, objectMapper, movieId, screenId, futureDate, "11:00:00", 5F);
        int seatId = TestHelper.createSeat(mockMvc, objectMapper, screenId, "A", (short) 1, SeatType.REGULAR, true);

        ReservationRequestDTO dto = new ReservationRequestDTO(showtimeId, seatId);

        mockMvc.perform(post("/reservations/lock")
                        .with(TestHelper.userPrincipal("test@test.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.seatRow").value("A"))
                .andExpect(jsonPath("$.seatColumn").value(1))
                .andExpect(jsonPath("$.status").value("LOCKED"))
                .andExpect(jsonPath("$.lockedBy").value("test@test.com"));
    }

    @Test
    void lockSeat_shouldReturn404_whenSeatNotFound() throws Exception {
        int movieId = TestHelper.createMovie(mockMvc, objectMapper, "Avatar", (short) 94, "Action");
        int theatreId = TestHelper.createTheatre(mockMvc, objectMapper, "Cineplexx", "Cara Konstantina 1", "Nis");
        int screenId = TestHelper.createScreen(mockMvc, objectMapper, theatreId, "IMAX 3D", (short) 100);
        String futureDate = java.time.LocalDate.now().plusDays(1).toString();
        int showtimeId = TestHelper.createShowtime(mockMvc, objectMapper, movieId, screenId, futureDate, "11:00:00", 5F);

        ReservationRequestDTO dto = new ReservationRequestDTO(showtimeId, 999);

        mockMvc.perform(post("/reservations/lock")
                        .with(TestHelper.userPrincipal("test@test.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Seat with id 999 not found"));
    }

    @Test
    void lockSeat_shouldReturn429_whenSeatLockLimitExceeded() throws Exception {
        int movieId = TestHelper.createMovie(mockMvc, objectMapper, "Avatar", (short) 94, "Action");
        int theatreId = TestHelper.createTheatre(mockMvc, objectMapper, "Cineplexx", "Cara Konstantina 1", "Nis");
        int screenId = TestHelper.createScreen(mockMvc, objectMapper, theatreId, "IMAX 3D", (short) 100);
        String futureDate = java.time.LocalDate.now().plusDays(1).toString();
        int showtimeId = TestHelper.createShowtime(mockMvc, objectMapper, movieId, screenId, futureDate, "11:00:00", 5F);

        int seatId1 = TestHelper.createSeat(mockMvc, objectMapper, screenId, "A", (short) 1, SeatType.REGULAR, true);
        int seatId2 = TestHelper.createSeat(mockMvc, objectMapper, screenId, "A", (short) 2, SeatType.REGULAR, true);
        int seatId3 = TestHelper.createSeat(mockMvc, objectMapper, screenId, "B", (short) 1, SeatType.REGULAR, true);
        int seatId4 = TestHelper.createSeat(mockMvc, objectMapper, screenId, "B", (short) 2, SeatType.REGULAR, true);

        TestHelper.lockSeat(mockMvc, objectMapper, showtimeId, seatId1, "test@test.com");
        TestHelper.lockSeat(mockMvc, objectMapper, showtimeId, seatId2, "test@test.com");
        TestHelper.lockSeat(mockMvc, objectMapper, showtimeId, seatId3, "test@test.com");

        ReservationRequestDTO dto = new ReservationRequestDTO(showtimeId, seatId4);

        mockMvc.perform(post("/reservations/lock")
                        .with(TestHelper.userPrincipal("test@test.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.message").value("Maximum allowed active seat locks exceeded (3)"));
    }

    @Test
    void lockSeat_shouldReturn404_whenShowtimeNotFound() throws Exception {
        int theatreId = TestHelper.createTheatre(mockMvc, objectMapper, "Cineplexx", "Cara Konstantina 1", "Nis");
        int screenId = TestHelper.createScreen(mockMvc, objectMapper, theatreId, "IMAX 3D", (short) 100);
        int seatId = TestHelper.createSeat(mockMvc, objectMapper, screenId, "A", (short) 1, SeatType.REGULAR, true);

        ReservationRequestDTO dto = new ReservationRequestDTO(999, seatId);


        mockMvc.perform(post("/reservations/lock")
                        .with(TestHelper.userPrincipal("test@test.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Showtime with id 999 not found"));
    }

    @Test
    void lockSeat_shouldReturn400_whenSeatNotInScreen() throws Exception {
        int movieId = TestHelper.createMovie(mockMvc, objectMapper, "Avatar", (short) 94, "Action");
        int theatreId = TestHelper.createTheatre(mockMvc, objectMapper, "Cineplexx", "Cara Konstantina 1", "Nis");
        int screenId1 = TestHelper.createScreen(mockMvc, objectMapper, theatreId, "IMAX 3D", (short) 100);
        String futureDate = java.time.LocalDate.now().plusDays(1).toString();
        int showtimeId = TestHelper.createShowtime(mockMvc, objectMapper, movieId, screenId1, futureDate, "11:00:00", 5F);

        int screenId2 = TestHelper.createScreen(mockMvc, objectMapper, theatreId, "CINEMAX 3D", (short)85);
        int seatId2 = TestHelper.createSeat(mockMvc, objectMapper, screenId2, "A", (short) 1, SeatType.REGULAR, true);

        ReservationRequestDTO dto = new ReservationRequestDTO(showtimeId, seatId2);

        mockMvc.perform(post("/reservations/lock")
                        .with(TestHelper.userPrincipal("test@test.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Seat " + seatId2 + " isn't inside screen " + screenId1));
    }

    @Test
    void lockSeat_shouldReturn409_whenSeatIsUnavailable() throws Exception {
        int movieId = TestHelper.createMovie(mockMvc, objectMapper, "Avatar", (short) 94, "Action");
        int theatreId = TestHelper.createTheatre(mockMvc, objectMapper, "Cineplexx", "Cara Konstantina 1", "Nis");
        int screenId = TestHelper.createScreen(mockMvc, objectMapper, theatreId, "IMAX 3D", (short) 100);
        String futureDate = java.time.LocalDate.now().plusDays(1).toString();
        int showtimeId = TestHelper.createShowtime(mockMvc, objectMapper, movieId, screenId, futureDate, "11:00:00", 5F);
        int seatId = TestHelper.createSeat(mockMvc, objectMapper, screenId, "A", (short) 1, SeatType.REGULAR, true);

        TestHelper.lockSeat(mockMvc, objectMapper, showtimeId, seatId, "test@test.com");

        ReservationRequestDTO dto = new ReservationRequestDTO(showtimeId, seatId);

        mockMvc.perform(post("/reservations/lock")
                        .with(TestHelper.userPrincipal("test@test.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Seat " + seatId + " is already locked or booked for showtime " + showtimeId));
    }

    @Test
    void lockSeat_shouldReturn401_whenNotAuthenticated() throws Exception {
        int movieId = TestHelper.createMovie(mockMvc, objectMapper, "Avatar", (short) 94, "Action");
        int theatreId = TestHelper.createTheatre(mockMvc, objectMapper, "Cineplexx", "Cara Konstantina 1", "Nis");
        int screenId = TestHelper.createScreen(mockMvc, objectMapper, theatreId, "IMAX 3D", (short) 100);
        String futureDate = java.time.LocalDate.now().plusDays(1).toString();
        int showtimeId = TestHelper.createShowtime(mockMvc, objectMapper, movieId, screenId, futureDate, "11:00:00", 5F);
        int seatId = TestHelper.createSeat(mockMvc, objectMapper, screenId, "A", (short) 1, SeatType.REGULAR, true);

        ReservationRequestDTO dto = new ReservationRequestDTO(showtimeId, seatId);

        mockMvc.perform(post("/reservations/lock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void lockSeat_shouldOnlyAllowOneUserToLock_whenConcurrentRequests() throws Exception {
        int movieId = TestHelper.createMovie(mockMvc, objectMapper, "Avatar", (short) 94, "Action");
        int theatreId = TestHelper.createTheatre(mockMvc, objectMapper, "Cineplexx", "Cara Konstantina 1", "Nis");
        int screenId = TestHelper.createScreen(mockMvc, objectMapper, theatreId, "IMAX 3D", (short) 100);
        int seatId = TestHelper.createSeat(mockMvc, objectMapper, screenId, "A", (short) 1, SeatType.REGULAR, true);
        String futureDate = LocalDate.now().plusDays(1).toString();
        int showtimeId = TestHelper.createShowtime(mockMvc, objectMapper, movieId, screenId, futureDate, "11:00:00", 5F);

        int threadCount = 5;
        try (ExecutorService executor = Executors.newFixedThreadPool(threadCount)) {
            CountDownLatch latch = new CountDownLatch(1);
            List<Integer> successStatuses = Collections.synchronizedList(new ArrayList<>());
            List<Integer> failStatuses = Collections.synchronizedList(new ArrayList<>());

            for (int i = 0; i < threadCount; i++) {
                final String email = "user" + i + "@test.com";
                executor.submit(() -> {
                    try {
                        latch.await();
                        UserPrincipal principal = new UserPrincipal(email, "Test User");
                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                principal, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

                        MvcResult result = mockMvc.perform(post("/reservations/lock")
                                        .with(SecurityMockMvcRequestPostProcessors.authentication(auth))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content("{\"showtimeId\":" + showtimeId + ",\"seatId\":" + seatId + "}"))
                                .andReturn();

                        if (result.getResponse().getStatus() == 201) {
                            successStatuses.add(201);
                        } else {
                            failStatuses.add(result.getResponse().getStatus());
                        }
                    } catch (Exception e) {
                        failStatuses.add(500);
                    }
                });
            }

            latch.countDown();
            executor.shutdown();
            boolean finished = executor.awaitTermination(10, TimeUnit.SECONDS);
            assertTrue(finished, "Threads did not finish within timeout");

            assertEquals(1, successStatuses.size());
            assertEquals(4, failStatuses.size());
        }
    }

    @Test
    void bookSeat_shouldReturn200_whenValidRequest() throws Exception {
        int movieId = TestHelper.createMovie(mockMvc, objectMapper, "Avatar", (short) 94, "Action");
        int theatreId = TestHelper.createTheatre(mockMvc, objectMapper, "Cineplexx", "Cara Konstantina 1", "Nis");
        int screenId = TestHelper.createScreen(mockMvc, objectMapper, theatreId, "IMAX 3D", (short) 100);
        String futureDate = LocalDate.now().plusDays(1).toString();
        int showtimeId = TestHelper.createShowtime(mockMvc, objectMapper, movieId, screenId, futureDate, "11:00:00", 5F);
        int seatId = TestHelper.createSeat(mockMvc, objectMapper, screenId, "A", (short) 1, SeatType.REGULAR, true);
        int reservationId = TestHelper.lockSeat(mockMvc, objectMapper, showtimeId, seatId, "test@test.com");

        BookingRequestDTO dto = new BookingRequestDTO(reservationId, "Marko Markovic", "1234567890123456", "12/27", "123");

        mockMvc.perform(patch("/reservations/book")
                        .with(TestHelper.userPrincipal("test@test.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seatRow").value("A"))
                .andExpect(jsonPath("$.seatColumn").value(1))
                .andExpect(jsonPath("$.status").value("BOOKED"))
                .andExpect(jsonPath("$.movieName").value("Avatar"))
                .andExpect(jsonPath("$.lastFourDigits").value("3456"))
                .andExpect(jsonPath("$.cardHolderName").value("Marko Markovic"));
    }

    @Test
    void bookSeat_shouldReturn404_whenReservationNotFound() throws Exception {
        BookingRequestDTO dto = new BookingRequestDTO(999, "Marko Markovic", "1234567890123456", "12/27", "123");

        mockMvc.perform(patch("/reservations/book")
                        .with(TestHelper.userPrincipal("test@test.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Seat reservation with id 999 not found"));
    }

    @Test
    void bookSeat_shouldReturn409_whenNotLocked() throws Exception {
        int movieId = TestHelper.createMovie(mockMvc, objectMapper, "Avatar", (short) 94, "Action");
        int theatreId = TestHelper.createTheatre(mockMvc, objectMapper, "Cineplexx", "Cara Konstantina 1", "Nis");
        int screenId = TestHelper.createScreen(mockMvc, objectMapper, theatreId, "IMAX 3D", (short) 100);
        String futureDate = LocalDate.now().plusDays(1).toString();
        int showtimeId = TestHelper.createShowtime(mockMvc, objectMapper, movieId, screenId, futureDate, "11:00:00", 5F);
        int seatId = TestHelper.createSeat(mockMvc, objectMapper, screenId, "A", (short) 1, SeatType.REGULAR, true);
        int reservationId = TestHelper.lockSeat(mockMvc, objectMapper, showtimeId, seatId, "test@test.com");

        TestHelper.bookSeat(mockMvc, objectMapper, reservationId, "test@test.com");

        BookingRequestDTO dto = new BookingRequestDTO(reservationId, "Marko Markovic", "1234567890123456", "12/27", "123");

        mockMvc.perform(patch("/reservations/book")
                        .with(TestHelper.userPrincipal("test@test.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Seat reservation " + reservationId + " is not in a locked state"));
    }

    @Test
    void bookSeat_shouldReturn409_whenNotOwnedByCurrentUser() throws Exception {
        int movieId = TestHelper.createMovie(mockMvc, objectMapper, "Avatar", (short) 94, "Action");
        int theatreId = TestHelper.createTheatre(mockMvc, objectMapper, "Cineplexx", "Cara Konstantina 1", "Nis");
        int screenId = TestHelper.createScreen(mockMvc, objectMapper, theatreId, "IMAX 3D", (short) 100);
        String futureDate = LocalDate.now().plusDays(1).toString();
        int showtimeId = TestHelper.createShowtime(mockMvc, objectMapper, movieId, screenId, futureDate, "11:00:00", 5F);
        int seatId = TestHelper.createSeat(mockMvc, objectMapper, screenId, "A", (short) 1, SeatType.REGULAR, true);

        int reservationId = TestHelper.lockSeat(mockMvc, objectMapper, showtimeId, seatId, "user1@test.com");

        BookingRequestDTO dto = new BookingRequestDTO(reservationId, "Marko Markovic", "1234567890123456", "12/27", "123");

        mockMvc.perform(patch("/reservations/book")
                        .with(TestHelper.userPrincipal("user2@test.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Seat reservation " + reservationId + " was not locked by the current user"));
    }

    @Test
    void bookSeat_shouldReturn409_whenLockExpired() throws Exception {
        int movieId = TestHelper.createMovie(mockMvc, objectMapper, "Avatar", (short) 94, "Action");
        int theatreId = TestHelper.createTheatre(mockMvc, objectMapper, "Cineplexx", "Cara Konstantina 1", "Nis");
        int screenId = TestHelper.createScreen(mockMvc, objectMapper, theatreId, "IMAX 3D", (short) 100);
        String futureDate = LocalDate.now().plusDays(1).toString();
        int showtimeId = TestHelper.createShowtime(mockMvc, objectMapper, movieId, screenId, futureDate, "11:00:00", 5F);
        int seatId = TestHelper.createSeat(mockMvc, objectMapper, screenId, "A", (short) 1, SeatType.REGULAR, true);
        int reservationId = TestHelper.lockSeat(mockMvc, objectMapper, showtimeId, seatId, "test@test.com");

        SeatReservation reservation = seatReservationRepository.findById(reservationId).orElseThrow();
        reservation.setLockedUntil(LocalDateTime.now().minusMinutes(10));
        seatReservationRepository.save(reservation);

        BookingRequestDTO dto = new BookingRequestDTO(reservationId, "Marko Markovic", "1234567890123456", "12/27", "123");

        mockMvc.perform(patch("/reservations/book")
                        .with(TestHelper.userPrincipal("test@test.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Seat reservation lock with id " + reservationId + " has expired"));
    }

    @Test
    void getMyReservations_shouldReturn200_withPageOfReservations() throws Exception {
        int movieId = TestHelper.createMovie(mockMvc, objectMapper, "Avatar", (short) 94, "Action");
        int theatreId = TestHelper.createTheatre(mockMvc, objectMapper, "Cineplexx", "Cara Konstantina 1", "Nis");
        int screenId = TestHelper.createScreen(mockMvc, objectMapper, theatreId, "IMAX 3D", (short) 100);
        String futureDate = LocalDate.now().plusDays(1).toString();
        int showtimeId = TestHelper.createShowtime(mockMvc, objectMapper, movieId, screenId, futureDate, "11:00:00", 5F);
        int seatId1 = TestHelper.createSeat(mockMvc, objectMapper, screenId, "A", (short) 1, SeatType.REGULAR, true);
        int seatId2 = TestHelper.createSeat(mockMvc, objectMapper, screenId, "A", (short) 2, SeatType.REGULAR, true);

        TestHelper.lockSeat(mockMvc, objectMapper, showtimeId, seatId1, "test@test.com");
        TestHelper.lockSeat(mockMvc, objectMapper, showtimeId, seatId2, "test@test.com");

        mockMvc.perform(get("/reservations/me")
                        .with(TestHelper.userPrincipal("test@test.com"))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.page.totalElements").value(2));
    }

    @Test
    void getReservationsByShowtime_shouldReturn200_withListOfReservations() throws Exception {
        int movieId = TestHelper.createMovie(mockMvc, objectMapper, "Avatar", (short) 94, "Action");
        int theatreId = TestHelper.createTheatre(mockMvc, objectMapper, "Cineplexx", "Cara Konstantina 1", "Nis");
        int screenId = TestHelper.createScreen(mockMvc, objectMapper, theatreId, "IMAX 3D", (short) 100);
        String futureDate = LocalDate.now().plusDays(1).toString();
        int showtimeId = TestHelper.createShowtime(mockMvc, objectMapper, movieId, screenId, futureDate, "11:00:00", 5F);
        int seatId1 = TestHelper.createSeat(mockMvc, objectMapper, screenId, "A", (short) 1, SeatType.REGULAR, true);
        int seatId2 = TestHelper.createSeat(mockMvc, objectMapper, screenId, "A", (short) 2, SeatType.REGULAR, true);

        TestHelper.lockSeat(mockMvc, objectMapper, showtimeId, seatId1, "test@test.com");
        TestHelper.lockSeat(mockMvc, objectMapper, showtimeId, seatId2, "test@test.com");

        mockMvc.perform(get("/reservations/" + showtimeId)
                        .with(TestHelper.adminPrincipal("admin@test.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getReservationsByEmail_shouldReturn200_withPageOfReservations() throws Exception {
        int movieId = TestHelper.createMovie(mockMvc, objectMapper, "Avatar", (short) 94, "Action");
        int theatreId = TestHelper.createTheatre(mockMvc, objectMapper, "Cineplexx", "Cara Konstantina 1", "Nis");
        int screenId = TestHelper.createScreen(mockMvc, objectMapper, theatreId, "IMAX 3D", (short) 100);
        String futureDate = LocalDate.now().plusDays(1).toString();
        int showtimeId = TestHelper.createShowtime(mockMvc, objectMapper, movieId, screenId, futureDate, "11:00:00", 5F);
        int seatId1 = TestHelper.createSeat(mockMvc, objectMapper, screenId, "A", (short) 1, SeatType.REGULAR, true);
        int seatId2 = TestHelper.createSeat(mockMvc, objectMapper, screenId, "A", (short) 2, SeatType.REGULAR, true);

        TestHelper.lockSeat(mockMvc, objectMapper, showtimeId, seatId1, "test@test.com");
        TestHelper.lockSeat(mockMvc, objectMapper, showtimeId, seatId2, "test@test.com");

        mockMvc.perform(get("/reservations")
                        .with(TestHelper.adminPrincipal("admin@test.com"))
                        .param("email", "test@test.com")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.page.totalElements").value(2));
    }

    @Test
    void cancelReservation_shouldReturn200_whenValidRequest() throws Exception {
        int movieId = TestHelper.createMovie(mockMvc, objectMapper, "Avatar", (short) 94, "Action");
        int theatreId = TestHelper.createTheatre(mockMvc, objectMapper, "Cineplexx", "Cara Konstantina 1", "Nis");
        int screenId = TestHelper.createScreen(mockMvc, objectMapper, theatreId, "IMAX 3D", (short) 100);
        String futureDate = LocalDate.now().plusDays(1).toString();
        int showtimeId = TestHelper.createShowtime(mockMvc, objectMapper, movieId, screenId, futureDate, "11:00:00", 5F);
        int seatId = TestHelper.createSeat(mockMvc, objectMapper, screenId, "A", (short) 1, SeatType.REGULAR, true);
        int reservationId = TestHelper.lockSeat(mockMvc, objectMapper, showtimeId, seatId, "test@test.com");
        TestHelper.bookSeat(mockMvc, objectMapper, reservationId, "test@test.com");

        mockMvc.perform(patch("/reservations/" + reservationId + "/cancel")
                        .with(TestHelper.adminPrincipal("admin@test.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELED"))
                .andExpect(jsonPath("$.seatRow").value("A"))
                .andExpect(jsonPath("$.seatColumn").value(1));
    }

    @Test
    void cancelReservation_shouldReturn404_whenNotFound() throws Exception {
        mockMvc.perform(patch("/reservations/999/cancel")
                        .with(TestHelper.adminPrincipal("admin@test.com")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Seat reservation with id 999 not found"));
    }

    @Test
    void cancelReservation_shouldReturn400_whenReservationNotBooked() throws Exception {
        int movieId = TestHelper.createMovie(mockMvc, objectMapper, "Avatar", (short) 94, "Action");
        int theatreId = TestHelper.createTheatre(mockMvc, objectMapper, "Cineplexx", "Cara Konstantina 1", "Nis");
        int screenId = TestHelper.createScreen(mockMvc, objectMapper, theatreId, "IMAX 3D", (short) 100);
        String futureDate = LocalDate.now().plusDays(1).toString();
        int showtimeId = TestHelper.createShowtime(mockMvc, objectMapper, movieId, screenId, futureDate, "11:00:00", 5F);
        int seatId = TestHelper.createSeat(mockMvc, objectMapper, screenId, "A", (short) 1, SeatType.REGULAR, true);
        int reservationId = TestHelper.lockSeat(mockMvc, objectMapper, showtimeId, seatId, "test@test.com");

        mockMvc.perform(patch("/reservations/" + reservationId + "/cancel")
                        .with(TestHelper.adminPrincipal("admin@test.com")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Seat reservation " + reservationId + " has not been booked and has no payment to cancel"));
    }



    @Test
    void getAvailableSeats_shouldReturn200_withCorrectAvailability() throws Exception {
        int movieId = TestHelper.createMovie(mockMvc, objectMapper, "Avatar", (short) 94, "Action");
        int theatreId = TestHelper.createTheatre(mockMvc, objectMapper, "Cineplexx", "Cara Konstantina 1", "Nis");
        int screenId = TestHelper.createScreen(mockMvc, objectMapper, theatreId, "IMAX 3D", (short) 100);
        String futureDate = LocalDate.now().plusDays(1).toString();
        int showtimeId = TestHelper.createShowtime(mockMvc, objectMapper, movieId, screenId, futureDate, "11:00:00", 5F);
        int seatId1 = TestHelper.createSeat(mockMvc, objectMapper, screenId, "A", (short) 1, SeatType.REGULAR, true);
        TestHelper.createSeat(mockMvc, objectMapper, screenId, "A", (short) 2, SeatType.REGULAR, true);

        TestHelper.lockSeat(mockMvc, objectMapper, showtimeId, seatId1, "test@test.com");

        mockMvc.perform(get("/showtimes/" + showtimeId)
                        .with(TestHelper.userPrincipal("test@test.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].status").value("UNAVAILABLE"))
                .andExpect(jsonPath("$[1].status").value("AVAILABLE"));
    }

    @Test
    void deleteShowtime_shouldCancelReservationsAndPayments_whenShowtimeDeleted() throws Exception {
        int movieId = TestHelper.createMovie(mockMvc, objectMapper, "Avatar", (short) 94, "Action");
        int theatreId = TestHelper.createTheatre(mockMvc, objectMapper, "Cineplexx", "Cara Konstantina 1", "Nis");
        int screenId = TestHelper.createScreen(mockMvc, objectMapper, theatreId, "IMAX 3D", (short) 100);
        String futureDate = LocalDate.now().plusDays(1).toString();
        int showtimeId = TestHelper.createShowtime(mockMvc, objectMapper, movieId, screenId, futureDate, "11:00:00", 5F);
        int seatId = TestHelper.createSeat(mockMvc, objectMapper, screenId, "A", (short) 1, SeatType.REGULAR, true);
        int reservationId = TestHelper.lockSeat(mockMvc, objectMapper, showtimeId, seatId, "test@test.com");
        TestHelper.bookSeat(mockMvc, objectMapper, reservationId, "test@test.com");

        mockMvc.perform(delete("/showtimes/" + showtimeId)
                        .with(TestHelper.adminPrincipal("admin@test.com")))
                .andExpect(status().isOk());

        assertTrue(seatReservationRepository.findById(reservationId).isEmpty());

        assertTrue(paymentRepository.findBySeatReservation_SeatReservationId(reservationId).isEmpty());
    }
}
