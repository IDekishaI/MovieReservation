# Movie Reservation API

A **Spring Boot REST API** for browsing movies, finding showtimes, and reserving cinema seats. The system handles the full reservation lifecycle — from seat locking with time-limited holds to payment confirmation — secured with **Google OAuth2 and JWT authentication**.

---

## Features

- **Movie & Theatre Browsing** — list all available movies and theatres
- **Showtime Discovery** — find future showtimes by theatre or movie
- **Seat Availability** — real-time seat availability per showtime with AVAILABLE/UNAVAILABLE status
- **Seat Locking** — lock a seat for 5 minutes while deciding, preventing double-booking
- **Concurrency Safe Locking** — pessimistic database locking prevents race conditions when multiple users attempt to lock the same seat simultaneously
- **Booking & Payment** — confirm a locked seat with card details, stores last 4 digits only
- **Reservation Management** — users can view their reservations; admins can cancel BOOKED reservations
- **Showtime Cancellation** — admin can delete a showtime, automatically cancelling all associated reservations and payments, notifying affected users via email
- **Email Notifications** — booking confirmation, cancellation, and showtime removal emails sent via Gmail SMTP
- **Automatic Lock Cleanup** — scheduled job runs every 60 seconds to purge expired locks
- **Redis Caching** — theatres, movies, and showtimes cached with per-cache TTLs to reduce database load
- **Rate Limiting** — Bucket4j token bucket algorithm protects high-traffic endpoints from abuse
- **Google OAuth2 Authentication** — login via Google, receive a JWT valid for 30 minutes
- **Role-Based Access Control** — USER and ADMIN roles with method-level security
- **Swagger UI** — fully documented and testable API at `/swagger-ui/index.html`
- **Integration Tests** — comprehensive test suite covering all endpoints, business logic, edge cases, and concurrency

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.0.1 |
| Security | Spring Security, OAuth2, JWT (JJWT 0.11.5) |
| Database | SQL Server |
| ORM | Spring Data JPA / Hibernate |
| Caching | Redis + Spring Cache |
| Rate Limiting | Bucket4j (token bucket algorithm) |
| Email | Spring Mail (Gmail SMTP) |
| Validation | Hibernate Validator |
| API Docs | SpringDoc OpenAPI (Swagger UI) |
| Testing | JUnit 5, MockMvc, H2 (in-memory) |
| Build | Maven |
| Utilities | Lombok |

---

## Architecture

```
┌─────────────────┐
│   Controllers   │  ← REST endpoints, request handling
├─────────────────┤
│    Services     │  ← Business logic, transactions
├─────────────────┤
│    Mappers      │  ← Entity ↔ DTO conversion
├─────────────────┤
│  Repositories   │  ← Data access layer (Spring Data JPA)
├─────────────────┤
│    Entities     │  ← JPA entities, database mapping
└─────────────────┘

Cross-cutting concerns:
├── RateLimiterAspect      ← AOP-based rate limiting via @RateLimit annotation
├── JwtAuthFilter          ← JWT validation on every request
├── GlobalExceptionHandler ← Centralized error responses
└── RedisConfig            ← Cache + rate limiter infrastructure
```

---

## API Endpoints

### Authentication
```
GET  /oauth2/authorization/google          # Redirect to Google login → returns JWT
```

### Movies
```
GET  /movies                               # Get all movies (paginated)
```

### Theatres
```
GET  /theatres                             # Get all theatres
```

### Showtimes
```
GET  /showtimes/theatre/{theatreId}        # Get future showtimes by theatre
GET  /showtimes/movie/{movieId}            # Get future showtimes by movie
GET  /showtimes/{showtimeId}               # Get seat availability for a showtime
```

### Reservations
```
GET    /reservations/me                    # Get current user's reservations (paginated)
POST   /reservations/lock                  # Lock a seat for 5 minutes
PATCH  /reservations/book                  # Book a locked seat with payment details
```

### Admin — Movies
```
POST    /movies                            # Create a movie
PUT     /movies/{movieId}                  # Update a movie
DELETE  /movies/{movieId}                  # Delete a movie (blocked if used in showtimes)
```

### Admin — Theatres
```
POST    /theatres                          # Create a theatre
PUT     /theatres/{theatreId}              # Update a theatre
DELETE  /theatres/{theatreId}              # Delete a theatre (blocked if has screens)
```

### Admin — Screens
```
GET     /screens                           # Get all screens (paginated)
POST    /screens                           # Create a screen
PUT     /screens/{screenId}                # Update a screen
DELETE  /screens/{screenId}                # Delete a screen (blocked if has showtimes or seats)
```

### Admin — Seats
```
GET     /seats                             # Get all seats (paginated)
GET     /seats/{screenId}                  # Get seats by screen
POST    /seats                             # Create a seat
PUT     /seats/{seatId}                    # Update a seat (blocked if has reservations)
DELETE  /seats/{seatId}                    # Delete a seat (blocked if has reservations)
```

### Admin — Showtimes
```
POST    /showtimes                         # Create a showtime (checks scheduling conflicts)
PUT     /showtimes/{showtimeId}            # Update a showtime
DELETE  /showtimes/{showtimeId}            # Delete showtime, cancel all reservations, notify users
```

### Admin — Reservations
```
GET     /reservations?email=               # Get reservations by user email (paginated)
GET     /reservations/{showtimeId}         # Get reservations by showtime
PATCH   /reservations/{id}/cancel          # Cancel a BOOKED reservation, delete payment, notify user
```

---

## Authentication Flow

```
1. Visit http://localhost:8080/oauth2/authorization/google
2. Sign in with your Google account
3. Receive JWT token in the response body
4. Click "Authorize" in Swagger UI and paste the token
5. All subsequent API calls are authenticated
```

Tokens expire after **30 minutes**. Re-authenticate via Google to get a new token.

---

## Installation & Setup

### Prerequisites

- Java 21+
- SQL Server
- Redis
- Maven 3.6+
- Google Cloud Console project with OAuth2 credentials
- Gmail account with App Password for email notifications

### Google OAuth2 Setup

1. Go to [console.cloud.google.com](https://console.cloud.google.com)
2. Create an OAuth2 Client ID (Web application)
3. Add `http://localhost:8080/login/oauth2/code/google` as an authorized redirect URI
4. Copy your client ID and secret

### Gmail App Password Setup

1. Enable 2-Factor Authentication on your Gmail account
2. Go to Google Account → Security → 2-Step Verification → App passwords
3. Generate an app password for Mail

### Redis Setup

Install Redis locally (Windows: [tporadowski/redis](https://github.com/tporadowski/redis/releases)) or run via Docker:
```bash
docker run -d --name movie-redis -p 6379:6379 redis:alpine
```

### Environment Variables

```bash
DB_USER=your_database_username
DB_PASS=your_database_password
JWT_SECRET=your_base64_encoded_secret
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret
TEST_GMAIL_USERNAME=your_gmail@gmail.com
TEST_GMAIL_APP_PASSWORD=your_16_char_app_password
```

To generate a secure JWT secret:
```
System.out.println(Base64.getEncoder().encodeToString(Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded()));
```

### Profiles

| Property | dev | prod |
|---|---|---|
| `ddl-auto` | `update` | `validate` |
| `show-sql` | `true` | `false` |

Active profile is set in `application.properties`:
```
spring.profiles.active=dev
```

### Run

```bash
git clone https://github.com/IDekishaI/MovieReservation
cd MovieReservation
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`.
Swagger UI is available at `http://localhost:8080/swagger-ui/index.html`.

### Run Tests

```bash
mvn test
```

Tests use an H2 in-memory database and mock Redis/email — no external dependencies needed.

---

## Database Schema

| Table | Description |
|---|---|
| `users` | Registered users via Google OAuth2 |
| `movie` | Movie catalogue |
| `theatre` | Cinema locations |
| `screen` | Screens within a theatre |
| `seat` | Individual seats per screen |
| `showtime` | Scheduled screenings |
| `seat_reservation` | Seat locks, bookings, and cancellations |
| `payment` | Payment records for confirmed bookings |

---

## Key Design Decisions

**Seat Locking with Pessimistic Locking** — seats are locked for 5 minutes before booking. Pessimistic database locking (`PESSIMISTIC_WRITE`) prevents race conditions where two users simultaneously lock the same seat. Expired locks are cleaned up every 60 seconds by a scheduled job, keeping the table lean without affecting real-time availability queries which filter by `lockedUntil` at query time.

**Booking History Preserved** — completed `BOOKED` and `CANCELED` records are never deleted. This maintains an audit trail for dispute resolution and keeps historical data available for future analytics. Only showtime deletion cascades a full removal.

**Showtime Deletion Cascade** — deleting a showtime automatically cancels all associated reservations, deletes payments, and sends cancellation emails to affected users. Emails are sent after all database operations succeed to avoid notifying users if the transaction fails.

**Redis Caching** — stable list endpoints (theatres, movies, showtimes by theatre/movie) are cached with per-cache TTLs (theatres 1hr, movies 30min, showtimes 10min). Cache is evicted on any write operation. Seat availability is intentionally not cached since it changes with every lock and booking.

**Rate Limiting** — Bucket4j token bucket algorithm limits requests per IP via a custom `@RateLimit` annotation and AOP aspect, making it trivial to protect any endpoint. Tokens refill gradually rather than resetting at fixed windows, preventing boundary exploitation.

**Stateless Authentication** — no server-side sessions. Google OAuth2 handles identity verification, after which a JWT is issued. Every subsequent request is authenticated purely via the token in the `Authorization` header.

**Payment Data Security** — only the last four digits of the card number are stored. CVV is never persisted. This follows standard PCI-DSS best practices even in a demonstration context.

**Scheduling Conflict Prevention** — creating or updating a showtime checks both the closest past and future showtimes on the same screen to prevent overlapping screenings, accounting for movie length.

**Validation** — all request bodies are validated via Hibernate Validator. Path variables are validated with `@Positive`. A centralized `GlobalExceptionHandler` handles all validation, type mismatch, and business logic errors with consistent JSON responses.

---

## Developer

**Dejan Tasic**
- GitHub: [IDekishaI](https://github.com/IDekishaI)
- LinkedIn: [Dejan Tasic](https://linkedin.com/in/tasicdejan-td1)
- Email: dejantasic2005@gmail.com