# 🏢 Enterprise Order System (Monolith Edition)

A consolidated, production-ready **Monolithic** application built with **Spring Boot** and **MySQL**. It replicates the identical business rules, transaction validations, and compensating rollbacks of the microservices edition, but executes synchronously inside a single runtime on port `8080` without requiring Apache Kafka, ZooKeeper, or an API Gateway.

---

## 🏗️ Architecture Overview

```
                        ┌──────────────────┐
                        │  Client / Postman│
                        └────────┬─────────┘
                                 │ HTTP Requests (Port 8080)
                                 ▼
                        ┌──────────────────┐
                        │    Monolithic    │
                        │ Spring Boot App  │
                        │  (Port 8080)     │
                        ├──────────────────┤
                        │  - User Domain   │
                        │  - Order Domain  │
                        │  - Stock Domain  │
                        │  - Pay Domain    │
                        └────────┬─────────┘
                                 │
                                 ▼
                        ┌──────────────────┐
                        │   MySQL DB       │
                        │ (enterprise_     │
                        │ monolith_db)     │
                        └──────────────────┘
```

---

## 📦 Tech Stack & Features

- **Framework**: Spring Boot 3.1.5 (Java 17/19 compatible)
- **Database**: MySQL 8.x (Single database: `enterprise_monolith_db`)
- **OR Mapper**: Spring Data JPA / Hibernate
- **Build Tool**: Maven
- **Port**: `8080`
- **Data Seeding**: Built-in self-contained Java database seeder (initializes seed users and stock levels on startup)

---

## 🚀 Getting Started

### Prerequisites
- ✅ **Java 17+** (JDK)
- ✅ **MySQL 8.x** running on `localhost:3306` with credentials `root/root`
- ✅ **Eclipse IDE** with Maven support
- ✅ **Postman** for API testing

---

### Step 1: Running the Application

You can start the application with one of two methods:

#### Method A: Double-Click Startup Script (Recommended)
1. Navigate to `C:\Users\pprat\eclipse-workspace\enterprise-order-system-monolith`.
2. Double-click **`START-MONOLITH.bat`**. This script sets your Java path, builds the project, downloads any missing maven dependencies, and boots the monolith on port `8080`.

#### Method B: Run from Eclipse IDE
1. Open **Eclipse**.
2. Go to **File → Import → Maven → Existing Maven Projects**.
3. Select root directory: `C:\Users\pprat\eclipse-workspace\enterprise-order-system-monolith`.
4. Click **Finish** and wait for Eclipse to build the workspace.
5. Right-click `EnterpriseMonolithApplication.java` → **Run As → Spring Boot App**.

---

## 🧪 Testing with Postman

Import the collection file: [`EnterpriseOrderSystem.postman_collection.json`](./EnterpriseOrderSystem.postman_collection.json)

### Scenario 1 — Happy Path (Successful Order) ✅
- **Request**: `POST http://localhost:8080/orders` with qty `2` and total `500.00`.
- **Flow**: Validates user, deducts stock (10 -> 8), saves completed payment, and updates order status directly to `CONFIRMED`.

### Scenario 2 — Out of Stock Failure ❌
- **Request**: `POST http://localhost:8080/orders` with qty `15` (which exceeds the available stock).
- **Flow**: Intercepts insufficient stock, creates the order record, and updates status directly to `FAILED` with reasoning `"Inventory reservation failed: Insufficient stock..."`.

### Scenario 3 — Payment Failure & Compensating Rollback ❌
- **Request**: `POST http://localhost:8080/orders` with total `1500.00` (> $1000.00 threshold limit).
- **Flow**: 
  1. Validates user and reserves stock (8 -> 5).
  2. Processes payment, which triggers a simulated payment rejection.
  3. Automatically rolls back the reserved stock (5 -> 8).
  4. Saves payment as `FAILED` and updates the order status to `FAILED` with reasoning `"Payment failed: Amount exceeds limit (simulation)"`.

---

## 📋 API Endpoint Reference

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/users` | `GET` | Fetch all seeded users |
| `/users/{id}` | `GET` | Fetch user details by ID |
| `/inventory/{productId}` | `GET` | Get product name and stock level |
| `/inventory/add` | `POST` | Replenish inventory stock |
| `/payments/order/{orderId}` | `GET` | Fetch payment transaction status for an order |
| `/orders/{id}` | `GET` | Fetch order status and rollback details |
| `/orders` | `POST` | Create a new order (with optional `X-Idempotency-Key` header) |

---

## 📁 Monolith Project Structure

```
enterprise-order-system-monolith/
├── pom.xml                                   # Dependency POM
├── START-MONOLITH.bat                        # Batch executable launcher
├── EnterpriseOrderSystem.postman_collection.json # API test suite
├── init-monolith.sql                         # Database setup script
│
├── src/main/java/com/enterprise/order/
│   ├── EnterpriseMonolithApplication.java    # Main Entry Point
│   │
│   ├── config/
│   │   └── DatabaseSeeder.java               # Automatic db seeder
│   │
│   ├── user/                                 # User domain
│   │   ├── model/User.java
│   │   ├── repository/UserRepository.java
│   │   ├── service/UserService.java
│   │   └── controller/UserController.java
│   │
│   ├── inventory/                            # Stock domain
│   │   ├── model/Inventory.java
│   │   ├── repository/InventoryRepository.java
│   │   ├── service/InventoryService.java
│   │   └── controller/InventoryController.java
│   │
│   ├── payment/                              # Payment domain
│   │   ├── model/Payment.java
│   │   ├── repository/PaymentRepository.java
│   │   ├── service/PaymentService.java
│   │   └── controller/PaymentController.java
│   │
│   └── order/                                # Order Orchestration
│       ├── model/Order.java
│       ├── repository/OrderRepository.java
│       ├── service/OrderService.java
│       └── controller/OrderController.java
```

---

## 👩‍💻 Author
**Prathyusha** — Enterprise Order System Monolith  
Eclipse Workspace: `C:\Users\pprat\eclipse-workspace\enterprise-order-system-monolith`
