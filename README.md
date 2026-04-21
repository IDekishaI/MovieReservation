# Movie Reservation API
> 🚧 **Work in Progress** — custom exceptions, pagination, rate limiting, integration tests, and showtime cancellation with email notifications coming soon.

A **Spring Boot REST API** for browsing movies, finding showtimes, and reserving cinema seats. The system handles the full reservation lifecycle — from seat locking with time-limited holds to payment confirmation — secured with **Google OAuth2 and JWT authentication**.

---

## Features

- **Movie & Theatre Browsing** — list all available movies and theatres
- **Showtime Discovery** — find future showtimes by theatre or movie
- **Seat Availability** — real-time seat availability per showtime with AVAILABLE/UNAVAILABLE status
- **Seat Locking** — lock a seat for 5 minutes while deciding, preventing double-booking
- **Booking & Payment** — confirm a locked seat with card details, stores last 4 digits only
- **Reservation Cancellation** — admin can cancel BOOKED reservations, deletes associated payment record
- **My Reservations** — authenticated users can view their own reservation history
- **Automatic Lock Cleanup** — scheduled job runs every 60 seconds to purge expired locks
- **Google OAuth2 Authentication** — login via Google, receive a JWT valid for 30 minutes
- **Swagger UI** — fully documented and testable API at `/swagger-ui/index.html`

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.0.1 |
| Security | Spring Security, OAuth2, JWT (JJWT 0.11.5) |
| Database | SQL Server |
| ORM | Spring Data JPA / Hibernate |
| Validation | Hibernate Validator |
| API Docs | SpringDoc OpenAPI (Swagger UI) |
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
```

---

## API Endpoints

### Authentication
```
GET  /oauth2/authorization/google          # Redirect to Google login → returns JWT
```

### Movies
```
GET  /movies                               # Get all movies
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
GET    /reservations/me                    # Get current user's reservations
POST   /reservations/lock                  # Lock a seat for 5 minutes
PATCH  /reservations/book                  # Book a locked seat with payment details
```

### Admin — Movies
```
POST    /movies                            # Create a movie
PUT     /movies/{movieId}                  # Update a movie
DELETE  /movies/{movieId}                  # Delete a movie
```

### Admin — Theatres
```
POST    /theatre                           # Create a theatre
PUT     /theatre/{theatreId}               # Update a theatre
DELETE  /theatre/{theatreId}               # Delete a theatre
```

### Admin — Screens
```
GET     /screens                           # Get all screens
POST    /screens                           # Create a screen
PUT     /screens/{screenId}                # Update a screen
DELETE  /screens/{screenId}                # Delete a screen
```

### Admin — Seats
```
GET     /seats                             # Get all seats
GET     /seats/{screenId}                  # Get seats by screen
POST    /seats                             # Create a seat
PUT     /seats/{seatId}                    # Update a seat
DELETE  /seats/{seatId}                    # Delete a seat
```

### Admin — Reservations
```
GET     /reservations?email=               # Get reservations by user email
GET     /reservations/{showtimeId}         # Get reservations by showtime
PATCH   /reservations/{id}/cancel          # Cancel a booking and delete payment record
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
- Maven 3.6+
- Google Cloud Console project with OAuth2 credentials

### Google OAuth2 Setup

1. Go to [console.cloud.google.com](https://console.cloud.google.com)
2. Create an OAuth2 Client ID (Web application)
3. Add `http://localhost:8080/login/oauth2/code/google` as an authorized redirect URI
4. Copy your client ID and secret

### Environment Variables

```bash
DB_USER=your_database_username
DB_PASS=your_database_password
JWT_SECRET=your_base64_encoded_secret
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret
```

To generate a secure JWT secret:
```
    System.out.println(Base64.getEncoder().encodeToString(
    Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded()));
```

### Profiles

The project uses Spring Boot profiles to separate dev and prod configuration.

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

**Seat Locking** — seats are locked for 5 minutes before booking to prevent two users from booking the same seat simultaneously. Expired locks are cleaned up automatically every 60 seconds by a scheduled job, keeping the table lean without affecting real-time availability queries which filter by `lockedUntil` at query time.

**Booking History Preserved** — completed `BOOKED` records are never deleted. This maintains an audit trail for dispute resolution and keeps historical data available for future analytics.

**Stateless Authentication** — no server-side sessions. Google OAuth2 handles identity verification, after which a JWT is issued. Every subsequent request is authenticated purely via the token in the `Authorization` header.

**Payment Data** — only the last four digits of the card number are stored. CVV is never persisted. This follows standard PCI-DSS best practices even in a demonstration context.

**Validation** — all request bodies are validated via Hibernate Validator. Path variables are validated with `@Positive`. A centralized `GlobalExceptionHandler` handles all validation, type mismatch, and business logic errors with consistent JSON responses.

---

## Developer

**Dejan Tasic**
- GitHub: [IDekishaI](https://github.com/IDekishaI)
- LinkedIn: [Dejan Tasic](https://linkedin.com/in/tasicdejan-td1)
- Email: dejantasic2005@gmail.com