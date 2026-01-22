# Smart Energy Management System ⚡

A distributed **Smart Energy Management System** built using a **microservices architecture**, providing real-time energy monitoring, intelligent alerts, and interactive chat support with AI integration.

---

## Project Overview

This project simulates and manages energy consumption for users and their devices. It allows:

* Secure user authentication and authorization
* Device management and real-time energy monitoring
* Automatic overconsumption detection
* Real-time notifications via WebSockets
* Chat support with rule-based and AI-powered responses
* Asynchronous communication between services using RabbitMQ

The system is designed following **event-driven architecture principles**, ensuring scalability, fault tolerance, and loose coupling between components.

---

## Architecture

### Microservices

| Service                        | Description                       | Port      |
| ------------------------------ | --------------------------------- | --------- |
| **Auth Service (demoA)**       | Authentication, JWT generation    | 8082      |
| **People Service (demoP)**     | User profile management           | 8080      |
| **Devices Service (demoD)**    | Device CRUD & assignment          | 8081      |
| **Monitoring Service (demoM)** | Energy measurements & alerts      | 8084      |
| **Chat Service (demoC)**       | WebSocket chat & notifications    | 8085      |
| **Simulator**                  | Generates energy consumption data | —         |
| **Frontend**                   | React SPA                         | 3000      |
| **Traefik**                    | API Gateway / Reverse Proxy       | 80 / 8080 |

---

## Technologies Used

### Backend

* Java 17
* Spring Boot
* Spring Security (JWT)
* Spring WebSocket (STOMP + SockJS)
* Spring AMQP (RabbitMQ)
* Hibernate / JPA
* PostgreSQL

### Frontend

* React
* JavaScript
* SockJS + STOMP Client

### Infrastructure

* Docker & Docker Compose
* RabbitMQ
* Traefik
* Gemini API (AI chatbot)

---

## Communication Patterns

### 1. REST (Synchronous)

* CRUD operations
* Authentication & authorization
* Admin actions

### 2. RabbitMQ (Asynchronous, Event-Driven)

Used for **inter-service communication**:

* User creation & deletion
* Device synchronization
* Energy measurements
* Overconsumption alerts

### 3. WebSocket (Real-Time)

Used for **client-facing communication**:

* Chat messages
* Live alerts
* Admin notifications

---

## RabbitMQ Queues

| Queue                | Purpose                         |
| -------------------- | ------------------------------- |
| `queuePeople`        | Sync user creation              |
| `queueDevices`       | Cascade delete users → devices  |
| `queueMonitoring`    | Register devices for monitoring |
| `queueMeasurements`  | Energy consumption data         |
| `queueNotifications` | Overconsumption alerts          |
| `queueAuthDeletion`  | User deletion sync              |

RabbitMQ ensures:

* Guaranteed message delivery
* Service decoupling
* Fault tolerance
* Scalability

---

## WebSocket & Chat System

* STOMP over SockJS
* Public chat channel
* Private user messages
* Admin escalation
* AI-powered responses via Gemini API
* Real-time alert delivery

---

## AI Integration

The chat system uses:

1. **Rule-based responses** (FAQ-style)
2. **AI responses** via Gemini API
3. **Admin escalation** for human support

Priority order:

```
Admin escalation → Rules → AI response
```

---

## Energy Monitoring & Alerts

1. Simulator sends measurements via RabbitMQ
2. Monitoring Service checks thresholds
3. Overconsumption detected
4. Alert sent to Chat Service
5. User notified instantly via WebSocket

Alerts are **not lost** even if services are temporarily offline.

---

## Security

* JWT-based authentication
* Stateless sessions
* Role-based authorization (ADMIN / USER)
* Secure filter chain with Spring Security

JWT claims include:

* username
* role
* userId (used across services)

---

## Running the Project

### Prerequisites

* Docker
* Docker Compose
* Java 17+
* Node.js (for frontend, if running separately)

### Start the system

```bash
docker-compose up -d
```

This will start:

* All microservices
* RabbitMQ
* PostgreSQL databases
* Traefik

### Frontend

```bash
cd frontend
npm install
npm start
```

Access the application at:

```
http://localhost
```

---

## Key Design Principles

* Microservices architecture
* Event-driven communication
* Loose coupling
* Scalability & resilience
* Clear separation of concerns
* Real-time user experience

---

## Why RabbitMQ + WebSocket?

* **RabbitMQ** guarantees reliable communication between backend services
* **WebSocket** provides instant feedback to users
* Together they ensure **both consistency and real-time responsiveness**

---