# 🛒 Antigravity Commerce

A full-stack, **realtime e-commerce platform** — Spring Boot + React — with live inventory, live order tracking, and a real payment flow. Built as a production-style reference app.

> **Live demo:** _coming soon (deployment in progress)_
> **Demo logins:** `admin@antigravity.com` / `admin123` &nbsp;·&nbsp; `customer@antigravity.com` / `password`

---

## ✨ Highlights

- **⚡ Realtime inventory** — when a customer buys, the "Only N left" badge updates **instantly** on every open product page (WebSocket / STOMP, no polling).
- **📦 Realtime order tracking** — when an admin advances an order (Paid → Packed → Shipped → Delivered), the customer's tracker moves **live**, no refresh.
- **💳 Real payment flow** — two-step create-order → verify-signature checkout via **Razorpay**, with a zero-config **simulated mode** so the app runs even without gateway keys.
- **🔐 JWT auth** with access + refresh tokens and role-based access (admin / customer).
- **🗂️ Rich catalog** — products, variants (SKU / price / stock), brands, categories, images, reviews.
- **🧱 Correct commerce logic** — stock is decremented transactionally and overselling is rejected.

## 🖼️ Screenshots

> _Add screenshots here — e.g. `docs/catalog.png`, `docs/product.png`, `docs/checkout.png`, `docs/order-tracking.png`._

| Storefront | Product (live stock) | Live order tracking |
|---|---|---|
| _screenshot_ | _screenshot_ | _screenshot_ |

## 🧰 Tech stack

**Backend:** Java 21, Spring Boot 3.3, Spring Security (JWT), Spring Data JPA, Spring WebSocket (STOMP), Flyway, MapStruct, PostgreSQL (H2 for local dev), Razorpay, springdoc/OpenAPI.

**Frontend:** React 19, TypeScript, Vite, Redux Toolkit, React Router, Tailwind CSS, @stomp/stompjs, Axios.

## 🏛️ Architecture

```
React (Vite)  ──REST──►  Spring Boot API  ──JPA/Flyway──►  PostgreSQL
     ▲                        │
     └──────WebSocket (STOMP)─┘   live stock + order status pushes
```

- Layered backend: `entity → repository → dto → mapper → service → controller`.
- Realtime via an in-memory STOMP broker; the API broadcasts to `/topic/stock` and `/topic/orders/{orderNumber}`.
- Schema owned by Flyway migrations (`V1`–`V6`); JPA only validates it.

## 🚀 Getting started

### Prerequisites
- **JDK 21**
- **Node 20+**
- (Optional) a PostgreSQL database — otherwise the app uses in-memory H2 for local dev.

### Backend
```bash
cd backend
# Local dev (in-memory H2, reseeds every run):
mvn spring-boot:run
# API on http://localhost:8080, Swagger UI at /swagger-ui.html
```

To run against **PostgreSQL** (persistent), create `backend/.env`:
```
DB_URL=jdbc:postgresql://<host>:5432/<db>?sslmode=require
DB_USERNAME=<user>
DB_PASSWORD=<password>
SPRING_PROFILES_ACTIVE=prod
```
Then export those variables and run `mvn spring-boot:run`.

**Payments (optional):** add `RAZORPAY_KEY_ID` / `RAZORPAY_KEY_SECRET` to enable the real gateway. Without them, checkout runs in **simulated mode** automatically.

### Frontend
```bash
cd frontend
npm install
npm run dev        # http://localhost:5173
```

## 🔑 Demo accounts
| Role | Email | Password |
|------|-------|----------|
| Admin | `admin@antigravity.com` | `admin123` |
| Customer | `customer@antigravity.com` | `password` |

## 📡 Key API endpoints
- `POST /api/v1/auth/login` · `POST /api/v1/auth/register`
- `GET  /api/v1/products` · `GET /api/v1/products/slug/{slug}`
- `POST /api/v1/cart/items`
- `POST /api/v1/payments/order` · `POST /api/v1/payments/verify`
- `GET  /api/v1/orders` · `PATCH /api/v1/admin/orders/{orderNumber}/status`
- WebSocket: connect to `/ws`, subscribe to `/topic/stock` and `/topic/orders/{orderNumber}`

Full interactive docs at **`/swagger-ui.html`** when the backend is running.

## 🗺️ Project status
See [`PROJECT_STATUS.md`](./PROJECT_STATUS.md) for the living build log. Foundation, persistence, payments, and the realtime layer are complete and verified; deployment is in progress.
