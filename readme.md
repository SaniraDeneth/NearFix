# 🚀 NearFix — Location-Based Service Marketplace

## 📌 Overview

**NearFix** is a microservices-based platform where service providers publish gigs and clients can find nearby services using map-based search.

It supports multiple service categories such as:

* Home services (plumbing, electrical)
* IT services
* Tutoring
* Freelance work

---

## 🎯 Core Idea

* Providers create **gigs** with location
* Clients search for **nearby services**
* System returns results based on **distance**

---

## 🧠 Architecture

### 🔹 Microservices Architecture

```
Frontend (React)
        ↓
API Gateway
        ↓
--------------------------------
| Auth Service                |
| User Service                |
| Gig Service (PostGIS ⭐)    |
| Order Service              |
--------------------------------
```

---

## 📦 Services

### 1. Auth Service

* User registration
* Login
* JWT generation

---

### 2. User Service

* User profile
* Role management (Client / Provider)

---

### 3. Gig Service ⭐ (Core)

* Create/update/delete gigs
* Store location (PostGIS)
* Nearby search
* Manage images & availability

---

### 4. Order Service

* Service requests
* Accept/reject orders
* Status tracking

---

### 5. API Gateway

* Single entry point
* JWT validation
* Route requests to services
* Inject headers (e.g., `X-User-Id`)

---

## 🗄️ Database Design

### 🔹 Each Service has its OWN database

* auth_db
* user_db
* gig_db (PostGIS)
* order_db

---

## 📍 Gig Service Database

### Tables:

* `gigs`
* `categories`
* `gig_images`
* `gig_availability`

---

### 🔹 Key Features

#### Location (PostGIS)

```sql
location GEOGRAPHY(POINT, 4326)
```

#### Nearby Search

```sql
ST_DWithin(location, point, radius)
```

---

### 🔹 Relationships

* Gig → Category (Many-to-One)
* Gig → Images (One-to-Many)
* Gig → Availability (One-to-Many)

---

### 🔹 Important Rule

* ✅ Use foreign keys inside service
* ❌ No foreign keys across services

---

## 🖼️ Image Handling

### V1 Approach

* Frontend sends image URLs

```json
{
  "imageUrls": ["https://image.jpg"]
}
```

### Future

* Upload to cloud (S3 / Cloudinary)

---

## ⏰ Availability Handling

Stored as:

* day (MONDAY, TUESDAY)
* start_time (TIME)
* end_time (TIME)

---

## 🔐 Authentication Flow

```
Client → API Gateway → Service
          ↓
     JWT validated
          ↓
   Extract userId
          ↓
 Add header: X-User-Id
```

---

## 📡 API Design (Gig Service)

```
POST   /gigs
GET    /gigs
GET    /gigs/{id}
PUT    /gigs/{id}
DELETE /gigs/{id}

GET    /gigs/nearby?lat=&lng=&radius=
```

---

## 🔗 Service Communication

### Method:

* REST APIs

### Example:

* Gig Service → User Service (fetch provider details)

---

## 🧠 Design Principles

* Thin controllers
* Business logic in service layer
* DTO-based communication
* Loose coupling between services
* Strong DB relations inside service

---

## 🐳 Dev Environment

### Docker Setup

* PostgreSQL containers
* PostGIS for Gig Service
* Each service runs separately

---

## 🛠️ Tech Stack

* Java 21
* Spring Boot
* Maven
* PostgreSQL + PostGIS
* Docker
* React (frontend)

---

## 📌 Development Plan

### Phase 1 (Current)

* Gig Service (core)
* Auth Service
* Basic User Service
* API Gateway

---

### Phase 2

* Order Service
* User integration
* Better DTOs

---

### Phase 3 (Future)

* Image upload service
* Chat system
* Notifications
* Payment integration

---

## ⚠️ What We Avoid (Intentionally)

* ❌ Overengineering
* ❌ Complex event systems
* ❌ Kafka (for now)
* ❌ File uploads in V1

---

## 💡 Key Highlight

NearFix is not just a CRUD app.

It includes:

* Microservices architecture
* Geo-spatial search (PostGIS)
* API Gateway pattern
* Real-world system design

---

## 🎯 Goal

Build a **production-level backend system** suitable for:

* Portfolio
* Interviews
* Real-world scalability

---

## 👨‍💻 Author Notes

This project is designed to:

* Learn microservices properly
* Avoid common beginner mistakes
* Focus on clean architecture

---

## 🚀 Next Steps

* Implement Gig Service fully
* Add validation & error handling
* Integrate User Service
* Build frontend map UI

---
