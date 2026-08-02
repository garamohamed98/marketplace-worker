# Marketplace Worker

A **marketplace automation worker**: a Ktor REST API that accepts listing submissions, stores them, queues them, and uses **Playwright (headful Chromium)** to automatically fill and submit the listing form on a mock marketplace web app.

## Architecture

The project uses a **clean, modular architecture** that can be read in two dimensions: **vertical** (feature-based modules) and **horizontal** (layers inside a feature).

### Vertical architecture — one independent module per feature

The code is organized **by feature**, not by technical layer. Each feature is a self-contained vertical slice under `src/main/kotlin/features/`, with everything it needs inside it. Today there is one feature — `listings` — but a new feature can be added or removed without touching the others:

```
src/main/kotlin/features/listings/        ← one complete, independent feature
├── ListingModule.kt                      ← composition root: wires service + registers routes
├── ListingRoutes.kt                      ← HTTP endpoints
├── service/                              ← business logic
├── repository/                           ← persistence (PostgreSQL + Redis)
├── domain/                               ← pure business model
├── dto/                                  ← request / response contracts
├── mapper/                               ← dto ⇄ domain conversion
├── database/                             ← table definition
├── messaging/                            ← RabbitMQ publisher + message DTO
└── worker/                               ← background consumer + Playwright publisher
```

Only the **shared infrastructure** — database, Redis, RabbitMQ and the Playwright browser — lives once, outside any feature, in `plugins/` (`DatabaseFactory`, `RedisFactory`, `RabbitFactory`, `BrowserFactory`), and is injected into the features.

### Horizontal architecture — layers inside a feature

Within a feature, the code is split into **horizontal layers**, each depending only on the layer below it:

```
ListingRoutes.kt             HTTP layer — thin, delegates to the service
        │
        ▼
ListingService.kt            Service layer — validation, business rules, orchestration
        │
        ├───────────────────────────────►  ListingMapper.kt (dto ⇄ domain)
        ▼
ListingRepository + RedisRepository      Repository layer — persistence abstraction
        │
        ▼
PostgreSQL (Exposed) · Redis · RabbitMQ · Playwright      Infrastructure
```

Layer by layer:

- **DTOs (`dto/`)** — `ListingRequest` (input) and `ListingStatusResponse` (output) are `@Serializable` contracts that cross the HTTP boundary, keeping the transport format decoupled from the internal model.
- **Mapper (`mapper/`)** — converts between DTO and domain (e.g. `ListingRequest.toDomain()`), so the domain model never leaks transport concerns.
- **Domain (`domain/`)** — the pure business model (`Listing`), with no framework dependencies.
- **Service (`service/`)** — owns the business rules: validation, persisting the listing, setting its status, publishing the queue message.
- **Repository (`repository/`)** — persistence abstraction: the `ListingRepository` interface with its `ListingRepositoryImpl` (Exposed/PostgreSQL) plus `RedisRepository` for status lookups.
- **Routing (`ListingRoutes.kt`)** — exposes the endpoints; it stays thin and forwards everything to the service.

### End-to-end flow

```
  Client (curl / Postman / app)
        │  POST /marketplace/listings
        ▼
  Ktor API (:8080) — ListingService
        ├─ 1. validate + save        ───►  PostgreSQL
        ├─ 2. status "QUEUED"        ───►  Redis
        └─ 3. publish { listingId }  ───►  RabbitMQ ("listing-publish")
        │  responds { jobId, status: "QUEUED" }
        ▼
  ListingWorker (background consumer)
        │  reads the listing from the DB
        ▼
  ListingPublisher + Playwright headful Chromium
        │  opens http://localhost:5173, fills #title / #price / #description, clicks Save
        ▼
  mock-market-place (React + Vite, :5173)
        │
        ▼
  GET /marketplace/listings/{id}/status   ← reads status from Redis
```

Step by step:

1. Client sends `POST /marketplace/listings` with `{ "title", "price", "description" }`.
2. `ListingService` validates, persists the listing in **PostgreSQL**, stores status `QUEUED` in **Redis**, and publishes a `{ listingId }` message to the RabbitMQ queue `listing-publish`.
3. The API immediately answers `{ "jobId": 1, "status": "QUEUED" }`.
4. `ListingWorker` consumes the message, loads the listing back from the database, and `ListingPublisher` drives a real **headful Chromium** window to `http://localhost:5173`, fills the form (`#title`, `#price`, `#description`) and clicks submit — exactly like a human would.
5. `GET /marketplace/listings/{id}/status` returns the current status from Redis.

### Stack

| Layer       | Technology |
|-------------|-----------|
| Language    | Kotlin 2.2.20 |
| Framework   | Ktor 3.5.1 (Tomcat engine) |
| Build       | Maven (wrapper `mvnw` included) |
| Database    | PostgreSQL 16 (Exposed ORM, HikariCP) |
| Cache/status| Redis 7 (Jedis) |
| Messaging   | RabbitMQ 3 (queue `listing-publish`) |
| Automation  | Playwright 1.54.0 + headful Chromium |
| Serialization | kotlinx.serialization JSON |
| Tests       | JUnit 5, Mockito, kotlin-test |
| Mock front  | React 18 + Vite 5 (`mock-market-place/`) |
| Infra       | Docker Compose (`docker-compose.yml`) |

### Repository layout

```
marketplace-worker/
├── docker-compose.yml              # PostgreSQL, RabbitMQ, Redis
├── api/                            # Ktor backend + worker
│   ├── pom.xml
│   ├── mvnw / mvnw.cmd             # Maven wrapper
│   └── src/
│       ├── main/kotlin/
│       │   ├── Application.kt      # Ktor module wiring
│       │   ├── main.kt             # Tomcat entry point
│       │   ├── plugins/            # DatabaseFactory, RedisFactory, RabbitFactory,
│       │   │                       # BrowserFactory (Playwright), Routing, StatusPages...
│       │   └── features/listings/
│       │       ├── ListingRoutes.kt        # REST endpoints
│       │       ├── ListingService.kt       # business logic
│       │       ├── repository/             # Exposed + Redis repositories
│       │       ├── messaging/              # RabbitMQ publisher + message DTO
│       │       ├── worker/ListingWorker.kt # queue consumer
│       │       ├── worker/ListingPublisher.kt # Playwright bot
│       │       └── database/domain/dto/mapper
│       ├── main/resources/application.yaml # port 8080, DB config
│       └── test/kotlin                     # JUnit + Mockito tests
└── mock-market-place/              # React/Vite app the bot publishes into
    ├── index.html                  # form: #title, #price, #description
    ├── src/App.jsx
    └── server.js                   # optional item store :3000 (data/items.json)
```

## Prerequisites

- **JDK 17+**
- **Maven 3.9+** (or use the bundled `mvnw` wrapper)
- **Docker + Docker Compose** (PostgreSQL, Redis, RabbitMQ)
- **Node.js 18+** (mock marketplace frontend)
- **Playwright Chromium** — installed in step 2 below

## Installation & running

### 1. Start the infrastructure (PostgreSQL, Redis, RabbitMQ)

```
docker compose up -d
```

### 2. Install the Playwright Chromium browser

The Playwright browser is downloaded using the Playwright CLI, invoked through the Maven `exec` plugin (`com.microsoft.playwright.CLI`). **Important:** because `mvn` is a `.cmd` batch file on Windows, unquoted `-D...=...` arguments get mangled, so the quoting differs by shell.

**PowerShell (5.1)** — use the `--%` stop-parsing operator plus quotes:

```
mvn --% "-Dmain.class=com.microsoft.playwright.CLI" "-Dexec.args=install chromium" exec:java
```

**CMD (Command Prompt)** — just quote each `-D` argument:

```
mvn "-Dmain.class=com.microsoft.playwright.CLI" "-Dexec.args=install chromium" exec:java
```

> Without the quotes/`--%`, the argument is split on `=` and you get `[ERROR] Unknown lifecycle phase` or the wrong main class (`com.garamohamed.MainKt`).

### 3. Start the mock marketplace (the site the bot publishes to)

Two processes run side by side, so open **two terminals**:

**Terminal 1 — the item store (port 3000):** persists submitted items to `mock-market-place/data/items.json`.

```
cd mock-market-place
npm install
npm run server
```

**Terminal 2 — the frontend (port 5173):** the page the Playwright bot fills and submits.

```
cd mock-market-place
npm run dev
```

The app is served at **http://localhost:5173**; Vite proxies `/api/*` requests to the item store on port **3000** (see `vite.config.js`).


## Try it

1. Open **http://localhost:5173** — you should see the "List an Item" form.
2. Submit a listing to the API.

**PowerShell:**

```powershell
Invoke-RestMethod -Method Post -Uri http://localhost:8080/marketplace/listings `
  -ContentType "application/json" `
  -Body '{"title":"Vintage Camera","price":199.99,"description":"Film camera in great condition"}'
```

**CMD / curl:**

```
curl -X POST http://localhost:8080/marketplace/listings ^
  -H "Content-Type: application/json" ^
  -d "{\"title\":\"Vintage Camera\",\"price\":199.99,\"description\":\"Film camera in great condition\"}"
```

3. You get back `{ "jobId": 1, "status": "QUEUED" }`.
4. Watch the terminal logs: `Worker received listing 1`, and the **Chromium window opens**, fills the form with your title/price/description and clicks **Save**. The new row appears in the mock marketplace table.
5. Check the status at any time:

```
curl http://localhost:8080/marketplace/listings/1/status
```

## Tests

```
cd api
mvn test
```

Unit/integration tests for the listings routes and service live in `api/src/test/kotlin`.

## Ports

| Service            | Port  |
|--------------------|-------|
| Ktor API           | 8080  |
| PostgreSQL         | 5433  |
| RabbitMQ           | 5672  |
| RabbitMQ UI        | 15672 |
| Redis              | 6379  |
| Mock marketplace   | 5173  |
| Item store         | 3000  |
