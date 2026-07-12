# Antigravity Commerce — Living Project Plan (SOURCE OF TRUTH)

> **Read this file at the START of every session before writing any code.**
> **Update the checkboxes in this file at the END of every task.**
> This file — not your memory — is the single source of truth for what is done and what is next.

## 0. Goal
A deployed, resume-worthy **realtime e-commerce app** with a live public link and clean git history.
Realtime features required: **live stock updates, live order tracking, real (test-mode) payments.**

Stack: Spring Boot (Java) + React 19 (Vite, Redux Toolkit, Tailwind). PostgreSQL. WebSocket for realtime.

## 1. Working Rules (NON-NEGOTIABLE)
1. **One task at a time.** Pick the FIRST unchecked item in the roadmap below. Do only that. Do not jump ahead.
2. **Commit after every completed task.** `git add -A && git commit -m "<phase>: <what changed>"`. Never leave work uncommitted.
3. **Verify before checking a box.** Backend: `mvn -q compile` must pass. Frontend: `npm run build` must pass. State the command output.
4. **No fake logic without a TODO in this file.** If you mock something (payment, stock), add it as an unchecked item here.
5. **If the plan and the code disagree, STOP and ask the user.** Do not invent new scope.
6. **Update this file** — tick the box and add a one-line note — as the last step of every task.

## 2. Current State (audited 2026-07-12)
- Backend: layered Spring Boot (entity/repo/dto/mapper/service/controller), JWT auth, Flyway V1–V4, catalog+variants, cart, checkout, orders, reviews, Swagger, Supabase storage. Solid.
- Frontend: React 19 + Redux Toolkit + Tailwind. Auth, catalog, product details, cart, checkout, orders, admin products/orders.
- **KNOWN GAPS / BUGS:**
  - [ ] Payment is still fake — every order is instantly `PAID` (Phase C fixes this).
  - [x] Stock is now decremented on checkout, and checkout is rejected if stock is insufficient.
  - [x] Checkout now uses the chosen variant's real price (not `findFirst`).
  - [x] Seed data now creates real variants with price + stock (was empty before).
  - [ ] Database is H2 in-memory — all data wiped on restart. Needs PostgreSQL (Phase B).
  - [ ] No realtime layer (no WebSocket/SSE) (Phase D).
  - [x] Now in git with a clean per-task history + `.gitignore`.

---

## 3. ROADMAP (do these top to bottom)

### Phase A — Foundation & Discipline
- [x] Initialize git for this project and make the first commit of the current working code.
- [x] Add a `.gitignore` (Java `target/`, node `node_modules/`, `.env`, IDE files).
- [x] Fix checkout bug: use the actual chosen variant's price, not `findFirst()`.
- [x] Fix checkout bug: decrement `ProductVariant.stockQuantity` inside the transaction; reject if insufficient stock.
- [x] Add cart-item → variant link so the customer's chosen variant flows through to the order.
- [x] Seed real product variants (price + stock) so the storefront actually has prices.

> NOTE: terminal builds need JDK 21. Set `JAVA_HOME` to `D:\Java Softwares\jdk-21.0.10` before running `mvn`
> (the default `JAVA_HOME` on this machine points to JDK 8, which cannot build this project).

### Phase B — Real Database (persistence)
- [ ] Add PostgreSQL dependency + `application-prod.yml` with env-var config.
- [ ] Run Postgres locally via Docker (`docker run postgres`) and confirm Flyway migrates cleanly against it.
- [ ] Verify data survives a backend restart.

### Phase C — Real Payments (test mode)
- [ ] Integrate a payment gateway in TEST mode (Razorpay recommended for India, or Stripe).
- [ ] Backend: create payment order endpoint + verify signature webhook; only mark order `PAID` after real confirmation.
- [ ] Frontend: checkout opens the gateway; on success, redirect to confirmation.

### Phase D — Realtime layer (the headline feature)
- [ ] Add Spring WebSocket (STOMP) config.
- [ ] Broadcast stock changes: when stock drops, push to a `/topic/product/{id}/stock` channel; catalog & product page update live.
- [ ] Broadcast order status: admin changes status → push to `/topic/orders/{orderId}`; customer's order page updates live without refresh.
- [ ] Frontend: connect via `@stomp/stompjs`; show a live "only N left" badge and a live order-status tracker.

### Phase E — Polish for showcase
- [ ] Loading skeletons, empty states, error toasts, mobile responsiveness pass.
- [ ] Seed realistic demo data (products with images, a demo admin + customer account).
- [ ] README with screenshots, feature list, live-demo link, and "how to run."

### Phase F — Deploy (live link)
- [ ] Dockerize backend; deploy backend + Postgres (Railway/Render).
- [ ] Deploy frontend (Vercel/Netlify) pointing at the live API.
- [ ] Configure CORS, env vars, HTTPS. Confirm the live link works end-to-end.

---

## 4. Change Log (newest first — agent appends one line per task)
- 2026-07-12: Phase A complete — fixed checkout (real variant price, stock decrement + insufficient-stock guard), linked CartItem/OrderItem → ProductVariant (migration V5), seeded real variants with price+stock, added .gitignore + untracked target/. Clean-compiled with JDK 21.
- 2026-07-12: Plan file created; project audited.
