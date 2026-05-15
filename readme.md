# 🚀 NearFix — Location-Based Service Marketplace (Backend V1)

Welcome to **NearFix**! This repository hosts the completed, production-grade **Backend Version 1** of a location-based microservice marketplace. NearFix enables service providers to publish geographic-specific gigs (e.g., plumbing, IT tutoring) and permits clients to seamlessly search and book local services using advanced map-based geo-queries.

NearFix is built using a modern **microservices architecture** with **Spring Boot 3**, **Spring Cloud Gateway**, and **PostGIS (spatial PostgreSQL database)**, fully containerized under **Docker Compose**.

---

## 🎯 Finished V1 Architecture

The platform consists of four dedicated containerized applications communicating via East-West REST APIs, routed globally through a secure edge API Gateway (North-South):

```
                 Client (React Web/Mobile App)
                             │
                             ▼
                        API Gateway (Port 8080)
                             │
       ┌─────────────────────┼─────────────────────┐
       ▼                     ▼                     ▼
 Auth Service (8082)   Gig Service (8081)    Order Service (8083)
  - auth_db             - gig_db (PostGIS)    - order_db
```

### 📦 Key Microservices

1. **`api-gateway` (Edge Proxy — Port `8080`):**
   * Single entry point mapping all public routes `/api/**`.
   * Enforces global, stateless JWT signature checks.
   * Extracts user details from tokens and injects clean `X-User-Id` headers downstream.
   * Integrates a Redis rate-limiter and route orchestrator.

2. **`auth-service` (Identity Provider — Port `8082`):**
   * Manages user registration, BCrypt credential hashing, logins, cookie-based token refreshes, and active session logouts.
   * Restricts critical role transitions through a whitelisted, internal-only `/upgrade-role` PUT endpoint guarded by a **Shared Secret Key** (blocking public hackers with **`403 Forbidden`**).

3. **`gig-service` ⭐ (Geospatial Engine — Port `8081`):**
   * Implements **PostGIS Geography coordinates** (`POINT, 4326`) for precision map searches.
   * Manages categories, gig details, availability schedules, and image galleries.
   * Features a transactional flow that automatically upgrades a `CLIENT` to a `PROVIDER` using a secure OpenFeign client when their first gig is published.
   * Fully covered by extensive unit and integration tests.

4. **`order-service` (Transaction Manager — Port `8083`):**
   * Handles service requests, checkout pipelines, and client-provider workflows.
   * Calculates real-time distance-based travel fees using Feign integrations.
   * **State-Machine Synchronization:** Automatically toggles provider/gig availability from *Active* ➔ *Busy* upon order acceptance, and automatically releases them back to *Active (available)* upon order completion or cancellation.
   * Fully verified by a comprehensive JUnit 5 and Mockito test suite.

---

## ⚡ Core Functionalities & Features

* **Geospatial Proximity Queries:** Utilizes `ST_DWithin` database queries to fetch gigs within a precise kilometer radius from the client's current coordinates.
* **Auto Role Escalation:** Implements frictionless onboarding. A client is automatically elevated to a Service Provider behind the scenes the moment they launch their first listing.
* **Service-to-Service Security:** Implements the **Shared Secret Header Pattern**. Downstream microservices authenticate inside the virtual network using secure, encrypted API tokens.
* **Stateless State Machine:** The platform manages provider busy/free states strictly based on real-time transaction phases (accepted, finished, or cancelled).

---

## 🛠️ Tech Stack

* **Language:** Java 17 (Temurin)
* **Framework:** Spring Boot 4.x, Spring Cloud 2025
* **Communication:** Spring Cloud OpenFeign
* **Databases:** PostgreSQL 15 + PostGIS Spatial Extensions (for geospatial queries), Redis 7 (gateway rate-limiting)
* **Testing:** JUnit 5, Mockito, AssertJ, Spring Boot Starter Test
* **Containerization:** Docker & Docker Compose

---

## 🚀 How to Run Locally

You can spin up the entire multi-database, multi-service backend stack in a single command.

### Prerequisites
* **Docker Desktop** installed and running.
* **Maven** (or use the provided `mvnw` wrapper in each service directory).

### Step-by-Step Instructions

1. **Clone the repository:**
   ```bash
   git clone <repo-url>
   cd NearFix
   ```

2. **Build all service packages:**
   Navigate into each microservice folder and compile the jars:
   ```bash
   cd auth-service && ./mvnw clean package -DskipTests && cd ..
   cd gig-service && ./mvnw clean package -DskipTests && cd ..
   cd order-service && ./mvnw clean package -DskipTests && cd ..
   cd api-gateway && ./mvnw clean package -DskipTests && cd ..
   ```

3. **Start the Docker Compose Stack:**
   Run the compose command from the root directory:
   ```bash
   docker compose up --build -d
   ```
   *This builds the Docker images, starts 3 PostgreSQL databases (with PostGIS on `gig-db`), spins up Redis, and starts the Gateway and all four services.*

4. **Verify container health:**
   ```bash
   docker compose ps
   ```
   *Ensure all containers are in the `running` state.*

---

## 🧪 Running Tests

We have implemented separate, robust test suites for the core business logic components.

* **To run Gig Service Tests:**
   ```bash
   cd gig-service
   ./mvnw test
   ```

* **To run Order Service Tests:**
   ```bash
   cd order-service
   ./mvnw test
   ```
   *Runs 12 Mockito unit tests verifying all checkout status transitions, self-booking guards, and distance pricing rules.*

---

## 📝 API Integration & Endpoints
Every public request must be routed through the edge Gateway on port **`8080`** using the prefix `/api`. 

Refer to the complete, detailed **[API Endpoints Guide](file:///d:/Sanira/projects/Near%20fix%20final/NearFix/apiEndpoints.md)** in the repository for request/response payloads for:
* Register / Login / Logout
* Creating & Searching Gigs Nearby
* Placing, Accepting, and Completing Orders
