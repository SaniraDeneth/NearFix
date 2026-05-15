# NearFix - Platform API Endpoints Guide

This guide details every endpoint exposed across the **NearFix Microservice Platform**, routed through the **API Gateway** on port `8080`.

---

## 🌐 Base Gateway URL
All client requests must go through the API Gateway:
* **Base URL**: `http://localhost:8080/api`

---

## 🔒 1. Authentication Service (`auth-service`)
**Context Path**: `/api/auth/**`

### A. Register User
Registers a new user account. By default, users register with the `CLIENT` role.

* **Method**: `POST`
* **URL**: `http://localhost:8080/api/auth/register`
* **Headers**:
  * `Content-Type: application/json`
* **Request Body**:
  ```json
  {
    "email": "user@example.com",
    "password": "strongPassword123"
  }
  ```
* **Response (201 Created)**:
  ```json
  {
    "id": "7b0f20da-7a1b-41c3-88bb-69271acb85cf",
    "email": "user@example.com",
    "role": "CLIENT"
  }
  ```

---

### B. Login User
Authenticates a user, returning a JWT access token in the response and setting an `HttpOnly` refresh token in a cookie.

* **Method**: `POST`
* **URL**: `http://localhost:8080/api/auth/login`
* **Headers**:
  * `Content-Type: application/json`
* **Request Body**:
  ```json
  {
    "email": "user@example.com",
    "password": "strongPassword123"
  }
  ```
* **Response (200 OK)**:
  * *Headers*: Sets `Set-Cookie: refreshToken=<JWT>; HttpOnly; Secure; Path=/`
  * *Body*:
    ```json
    {
      "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwidXNlcklkIjoiN2IwZjIwZGEtN2ExYi00MWMzLTg4YmItNjkyNzFhY2I4NWNmIiwicm9sZSI6IkNMSUVOVCJ9..."
    }
    ```

---

### C. Refresh Access Token
Issues a new JWT access token using the active `refreshToken` cookie.

* **Method**: `POST`
* **URL**: `http://localhost:8080/api/auth/refresh`
* **Headers**:
  * *Required*: Must include the `refreshToken` Cookie from the login response.
* **Response (200 OK)**:
  ```json
  {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOi..."
  }
  ```

---

### D. Logout User
Invalidates the current session and clears the `refreshToken` cookie.

* **Method**: `POST`
* **URL**: `http://localhost:8080/api/auth/logout`
* **Headers**:
  * *Required*: Must include the `refreshToken` Cookie.
* **Response (204 No Content)**: Empty body with `Set-Cookie` header clearing the `refreshToken` cookie.

---

## 💼 2. Gig Service (`gig-service`)
**Context Path**: `/api/gigs/**` and `/api/categories/**`

### A. Categories
#### 1. Create Category
* **Method**: `POST`
* **URL**: `http://localhost:8080/api/categories`
* **Request Body**:
  ```json
  {
    "name": "Plumbing",
    "parentId": null
  }
  ```
* **Response (201 Created)**:
  ```json
  {
    "id": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
    "name": "Plumbing",
    "parentId": null
  }
  ```

#### 2. Get All Categories
* **Method**: `GET`
* **URL**: `http://localhost:8080/api/categories`
* **Response (200 OK)**: Array of `CategoryDto` objects.

#### 3. Get Category by ID
* **Method**: `GET`
* **URL**: `http://localhost:8080/api/categories/{id}`
* **Response (200 OK)**: `CategoryDto` object.

#### 4. Update Category
* **Method**: `PUT`
* **URL**: `http://localhost:8080/api/categories/{id}`
* **Request Body**:
  ```json
  {
    "name": "Plumbing Services"
  }
  ```
* **Response (200 OK)**: Updated `CategoryDto` object.

#### 5. Delete Category
* **Method**: `DELETE`
* **URL**: `http://localhost:8080/api/categories/{id}`
* **Response (204 No Content)**

---

### B. Gigs

#### 1. Create Gig (Publishing Flow)
Creates a new service listing (gigs) with multi-mode options, pricing types, and active availability schedules.

* **Method**: `POST`
* **URL**: `http://localhost:8080/api/gigs`
* **Headers**:
  * `Content-Type: application/json`
  * `X-user-Id: 2c9fd3e6-9b57-41a4-bd71-c0135351ac2f` *(Provider's User ID, automatically injected by gateway)*
* **Request Body**:
  ```json
  {
    "title": "Professional Emergency Plumbing Services",
    "description": "Offering quick, premium-quality plumbing fixes and leak repairs.",
    "categoryId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
    "modes": [
      "VISIT_CLIENT",
      "VISIT_PROVIDER"
    ],
    "pricing": {
      "basePrice": 2500.00,
      "travelFeePerKm": 50.00,
      "priceType": "FIXED",
      "maxVisitRadiusKm": 25
    },
    "location": {
      "lat": 6.9271,
      "lng": 79.8612
    },
    "availabilities": [
      {
        "days": ["MONDAY", "TUESDAY"],
        "startTime": "08:00:00",
        "endTime": "17:00:00"
      }
    ],
    "imageUrls": [
      "https://example.com/images/plumbing-1.jpg"
    ]
  }
  ```
* **Response (201 Created)**:
  ```json
  {
    "id": "2abc2e74-5b6a-4be9-9d72-0451c1c6f530",
    "title": "Professional Emergency Plumbing Services",
    "modes": [
      "VISIT_CLIENT",
      "VISIT_PROVIDER"
    ],
    "pricing": {
      "basePrice": 2500.00,
      "travelFeePerKm": 50.00,
      "priceType": "FIXED",
      "maxVisitRadiusKm": 25
    },
    "category": {
      "id": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
      "name": "Plumbing",
      "parentId": null
    },
    "location": {
      "lat": 6.9271,
      "lng": 79.8612
    },
    "imageUrls": [
      "https://example.com/images/plumbing-1.jpg"
    ],
    "availabilities": [
      {
        "id": "11111111-2222-3333-4444-555555555555",
        "gigId": "2abc2e74-5b6a-4be9-9d72-0451c1c6f530",
        "availableDay": "MONDAY",
        "startTime": "08:00:00",
        "endTime": "17:00:00"
      },
      {
        "id": "22222222-3333-4444-5555-666666666666",
        "gigId": "2abc2e74-5b6a-4be9-9d72-0451c1c6f530",
        "availableDay": "TUESDAY",
        "startTime": "08:00:00",
        "endTime": "17:00:00"
      }
    ],
    "providerId": "2c9fd3e6-9b57-41a4-bd71-c0135351ac2f",
    "isAvailable": true
  }
  ```

#### 2. Get All Gigs
* **Method**: `GET`
* **URL**: `http://localhost:8080/api/gigs`
* **Response (200 OK)**: Array of `GigDto` objects.

#### 3. Search Gigs Nearby
Performs proximity search for available gigs within a radius, applying category and price filters.
* **Method**: `GET`
* **URL**: `http://localhost:8080/api/gigs/search`
* **Query Parameters**: `lat`, `lng`, `radiusInKm`, `categoryId`, `minPrice`, `maxPrice`
* **Response (200 OK)**: Array of `GigDto` objects.

#### 4. Get Gig by ID
* **Method**: `GET`
* **URL**: `http://localhost:8080/api/gigs/{id}`
* **Response (200 OK)**: `GigDto` object.

#### 5. Update Gig
* **Method**: `PUT`
* **URL**: `http://localhost:8080/api/gigs/{id}`
* **Headers**: `X-user-Id`
* **Request Body**: `UpdateGigRequest` JSON
* **Response (200 OK)**: Updated `GigDto`.

#### 6. Delete Gig
* **Method**: `DELETE`
* **URL**: `http://localhost:8080/api/gigs/{id}`
* **Headers**: `X-user-Id`
* **Response (204 No Content)**

#### 7. Update Gig Availability Status
Toggles the provider's active status (`isAvailable`).
* **Method**: `PUT`
* **URL**: `http://localhost:8080/api/gigs/{id}/availability?available=true`
* **Response (200 OK)**: Updated `GigDto`.

#### 8. Calculate Visit Fee
* **Method**: `GET`
* **URL**: `http://localhost:8080/api/gigs/{id}/calculate-visit-fee?lat={lat}&lng={lng}`
* **Response (200 OK)**: Numeric fee amount.

---

### C. Gig Images
#### 1. Add Image to Gig
* **Method**: `POST`
* **URL**: `http://localhost:8080/api/gigs/{gigId}/images`
* **Headers**: `X-user-Id`
* **Request Body**: `{ "imageUrl": "..." }`
* **Response (200 OK)**: `GigImageDto` object.

#### 2. Get Gig Images
* **Method**: `GET`
* **URL**: `http://localhost:8080/api/gigs/{gigId}/images`
* **Response (200 OK)**: Array of `GigImageDto`.

#### 3. Delete Gig Image
* **Method**: `DELETE`
* **URL**: `http://localhost:8080/api/gigs/images/{imageId}`
* **Headers**: `X-user-Id`
* **Response (204 No Content)**

---

### D. Gig Availabilities
#### 1. Add Availability to Gig
* **Method**: `POST`
* **URL**: `http://localhost:8080/api/gigs/{gigId}/availabilities`
* **Headers**: `X-user-Id`
* **Request Body**: 
  ```json
  {
    "days": ["WEDNESDAY"],
    "startTime": "09:00:00",
    "endTime": "13:00:00"
  }
  ```
* **Response (200 OK)**: Array of added `GigAvailabilityDto`.

#### 2. Get Gig Availabilities
* **Method**: `GET`
* **URL**: `http://localhost:8080/api/gigs/{gigId}/availabilities`
* **Response (200 OK)**: Array of `GigAvailabilityDto`.

#### 3. Delete Gig Availability
* **Method**: `DELETE`
* **URL**: `http://localhost:8080/api/gigs/availabilities/{availabilityId}`
* **Headers**: `X-user-Id`
* **Response (204 No Content)**

---

## 🛒 3. Order Service (`order-service`)
**Context Path**: `/api/orders/**`

### A. Create Order (Checkout Flow)
Places a new booking request. Calculates dynamic travel fees if mode is `VISIT_CLIENT`. Prevents self-bookings.

* **Method**: `POST`
* **URL**: `http://localhost:8080/api/orders`
* **Headers**:
  * `Content-Type: application/json`
  * `X-user-Id: 1757266a-d560-4edc-9cf3-221f05edf872` *(Client's User ID)*
* **Request Body**:
  ```json
  {
    "gigId": "2abc2e74-5b6a-4be9-9d72-0451c1c6f530",
    "serviceMode": "VISIT_CLIENT",
    "clientAddress": "123 Galle Road, Colombo 03, Sri Lanka",
    "clientLatitude": 6.9271,
    "clientLongitude": 79.8612
  }
  ```
* **Response (201 Created)**:
  ```json
  {
    "id": "b041cef3-bb8d-42c5-b890-766c20202eb3",
    "clientId": "1757266a-d560-4edc-9cf3-221f05edf872",
    "providerId": "658f17de-e9fe-4135-9639-e5be3818364b",
    "gigId": "2abc2e74-5b6a-4be9-9d72-0451c1c6f530",
    "categoryId": "1ed810b5-51fb-489f-bba1-478c560022db",
    "serviceMode": "VISIT_CLIENT",
    "basePrice": 2500.00,
    "travelFee": 350.00,
    "totalPrice": 2850.00,
    "clientAddress": "123 Galle Road, Colombo 03, Sri Lanka",
    "clientLatitude": 6.9271,
    "clientLongitude": 79.8612,
    "status": "PENDING",
    "createdAt": "2026-05-27T13:27:43.045",
    "updatedAt": "2026-05-27T13:27:43.045"
  }
  ```

---

### B. Accept Order
Allows a provider to accept a pending request, which automatically flags their availability as busy (`available=false`).

* **Method**: `PUT`
* **URL**: `http://localhost:8080/api/orders/{id}/accept`
* **Headers**:
  * `X-user-Id: 658f17de-e9fe-4135-9639-e5be3818364b` *(Must match the order's providerId)*
* **Response (200 OK)**: Returns the updated `OrderDto` with `"status": "ACCEPTED"`.

---

### C. Reject Order
Allows a provider to reject a pending request.

* **Method**: `PUT`
* **URL**: `http://localhost:8080/api/orders/{id}/reject`
* **Headers**:
  * `X-user-Id: 658f17de-e9fe-4135-9639-e5be3818364b` *(Must match the order's providerId)*
* **Response (200 OK)**: Returns the updated `OrderDto` with `"status": "REJECTED"`.

---

### D. Complete Order
Completes the booking, automatically releasing the provider's availability back to free (`available=true`).

* **Method**: `PUT`
* **URL**: `http://localhost:8080/api/orders/{id}/complete`
* **Headers**:
  * `X-user-Id: 658f17de-e9fe-4135-9639-e5be3818364b` *(Must match the order's providerId)*
* **Response (200 OK)**: Returns the updated `OrderDto` with `"status": "COMPLETED"`.

---

### E. Cancel Order
Allows either the client or the provider to cancel an active order. If the order was already accepted, it automatically releases the provider (`available=true`).

* **Method**: `PUT`
* **URL**: `http://localhost:8080/api/orders/{id}/cancel`
* **Headers**:
  * `X-user-Id: 1757266a-d560-4edc-9cf3-221f05edf872` *(Must be the associated clientId or providerId)*
* **Response (200 OK)**: Returns the updated `OrderDto` with `"status": "CANCELLED"`.

---

### F. Get Client Orders
Retrieves all orders placed by the current client.

* **Method**: `GET`
* **URL**: `http://localhost:8080/api/orders/client`
* **Headers**:
  * `X-user-Id: 1757266a-d560-4edc-9cf3-221f05edf872`
* **Response (200 OK)**: Array of `OrderDto` JSON objects.

---

### G. Get Provider Orders
Retrieves all orders received by the current provider.

* **Method**: `GET`
* **URL**: `http://localhost:8080/api/orders/provider`
* **Headers**:
  * `X-user-Id: 658f17de-e9fe-4135-9639-e5be3818364b`
* **Response (200 OK)**: Array of `OrderDto` JSON objects.
