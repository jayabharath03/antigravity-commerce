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
- [x] Add PostgreSQL dependency + `application-prod.yml` with env-var config. (Driver + flyway-postgres were already in pom; added the `prod` profile + `SPRING_PROFILES_ACTIVE` override + `.env.example`.)
- [x] Point `prod` profile at a free cloud Postgres (Neon) — Flyway applied all 5 migrations cleanly.
- [x] Verified data survives a backend restart (restart → "No migration necessary", seeder skipped, products still served).
- [x] Fixed a pre-existing broken unit test (`ProductServiceImplTest.searchProducts` used an outdated signature). Full suite now green: 10/10.

> HOW TO RUN WITH POSTGRES (prod profile), from `backend/`:
> ```bash
> export JAVA_HOME="/d/Java Softwares/jdk-21.0.10"
> while IFS='=' read -r k v; do [ -n "$k" ] && export "$k=$v"; done < <(sed 's/\r$//' .env | grep -vE '^\s*#|^\s*$')
> mvn -Dmaven.test.skip=true spring-boot:run
> ```
> `.env` gotcha: keep DB credentials in `DB_USERNAME`/`DB_PASSWORD` (NOT inside the JDBC URL — the driver rejects `user:pass@host`). Use LF line endings.

### Phase C — Real Payments (test mode) — Razorpay
- [x] Integrate Razorpay SDK (test mode). Config via `RAZORPAY_KEY_ID` / `RAZORPAY_KEY_SECRET` env vars.
- [x] Backend: `POST /api/v1/payments/order` (creates Razorpay order for cart total) + `POST /api/v1/payments/verify` (verifies HMAC signature, then places order). Order stores `razorpay_order_id`/`razorpay_payment_id` (migration V6). Stock only decremented after verified payment.
- [x] Frontend: checkout opens the Razorpay popup; on success it verifies server-side, then redirects to confirmation.
- [x] Graceful fallback: with NO keys set, payment runs in SIMULATED mode (no external account needed). Auto-switches to real Razorpay the moment `RAZORPAY_KEY_ID`/`RAZORPAY_KEY_SECRET` are set — no code change.
- [x] VERIFIED end-to-end via API on Neon: login → add to cart → /payments/order (mock) → /payments/verify → order PAID, stock decremented 25→23, total ₹2637.80.
- [ ] OPTIONAL (later): add real Razorpay test keys and run a test-card checkout in the browser (Razorpay dashboard was down when we tried).

### Phase D — Realtime layer (the headline feature) — DONE & VERIFIED
- [x] Add Spring WebSocket (STOMP) config — endpoint `/ws`, broker `/topic` (WebSocketConfig, permitted in SecurityConfig).
- [x] Broadcast stock changes on `/topic/stock` when an order decrements stock (RealtimeEventPublisher, hooked in CheckoutServiceImpl). VERIFIED: subscriber received `{stockQuantity:22}` on order.
- [x] Broadcast order status on `/topic/orders/{orderNumber}` when admin updates it (hooked in OrderServiceImpl). VERIFIED: subscriber received `{status:"DELIVERED"}`.
- [x] Frontend: `@stomp/stompjs` client (`utils/realtime.ts`); ProductDetails shows a live "Only N left" badge; OrderDetails shows a live status stepper. Both build clean.

### Phase E2 — Real storefront UX (from user testing feedback)
User feedback after browser testing: ugly `alert()`s, inconsistent light/dark themes, no shared navbar, broken links, out-of-stock still addable, admin add/edit not working, "doesn't look like a real store."
- [x] Shared **Navbar + Footer + Layout** on every page (persistent nav, cart count, account/logout, admin link).
- [x] **Unified light theme** — converted the dark Catalog & ProductDetails to match the rest (Amazon/Flipkart-style).
- [x] Replaced all `alert()` with clean **toasts** (`components/Toast.tsx`).
- [x] Fixed broken links: "Start Shopping" → /catalog, "Track Order" → /orders/{id}, catch-all route → /catalog (no more blank pages).
- [x] **Stock enforcement** on catalog: out-of-stock can't be added, low-stock/out-of-stock badges on cards; backend errors surface as toasts.
- [ ] Admin **add/edit product** — needs backend ProductRequest to accept price/stock/image (currently base-product only), then a real form. IN PROGRESS.
- [ ] NOTE for user: to see orders persist in Neon, run backend with the `prod` profile (SPRING_PROFILES_ACTIVE=prod + .env). Plain `mvn spring-boot:run` uses in-memory H2 (wiped on restart) — that's why Neon looked empty.

### Phase E — Polish for showcase
- [x] Seed realistic demo data — 8 products with images, varied stock (25/15/0/5/12/4/3/50) to show in-stock, low-stock & out-of-stock badges; demo admin + customer accounts. Verified via API.
- [x] README with feature list, tech stack, architecture, how-to-run, demo logins, API map (screenshots placeholder for user to add).
- [ ] (Optional) deeper UI polish: skeletons/toasts/mobile pass. App already has loading spinners + Tailwind styling; revisit if time allows.

### Phase F — Deploy (live link)
- [x] Deploy prep (code): backend `Dockerfile`, `server.port=${PORT}`, configurable CORS (`CORS_ALLOWED_ORIGINS`), configurable frontend API/WS URLs (`VITE_API_URL`/`VITE_WS_URL`), frontend `.env.example`. Both build clean.
- [ ] Deploy backend + point at Neon Postgres (Render/Railway). Set env: DB_*, SPRING_PROFILES_ACTIVE=prod, CORS_ALLOWED_ORIGINS, (optional RAZORPAY_*).  ← NEEDS USER account.
- [ ] Deploy frontend (Vercel/Netlify) with VITE_API_URL + VITE_WS_URL pointing at the deployed backend.  ← NEEDS USER account.
- [ ] Set backend CORS to the deployed frontend URL; confirm the live link works end-to-end (login, buy, realtime).

---

## 4. Change Log (newest first — agent appends one line per task)
- 2026-07-13: Phase F prep — backend Dockerfile, server.port=${PORT}, env-driven CORS, env-driven frontend API/WS URLs + .env.example. Ready to deploy; hosting needs user accounts.
- 2026-07-13: Phase E (core) — expanded seeder to 8 products with images + varied stock (verified 8 via API on H2), wrote full README (features/stack/architecture/run/demo logins). Optional deeper UI polish deferred.
- 2026-07-13: Phase D VERIFIED — Spring WebSocket/STOMP realtime. Live stock (/topic/stock) + live order status (/topic/orders/{no}) both confirmed end-to-end with a raw STOMP subscriber. Frontend: realtime.ts + live stock badge (ProductDetails) + live status stepper (OrderDetails). @stomp/stompjs added.
- 2026-07-13: Phase C VERIFIED (simulated mode) — added no-account fallback (mock payment when keys absent; auto-upgrades to real Razorpay when keys set). End-to-end API test on Neon passed: order PAID + stock 25→23. User couldn't create Razorpay account (dashboard outage), so simulated mode is the current default.
- 2026-07-12: Phase C (code) — Razorpay two-step payment: backend /payments/order + /payments/verify (HMAC signature check), Order stores razorpay refs (V6), checkout refactored so stock/order only happen after verified payment; frontend opens Razorpay popup then verifies. Backend + frontend compile. Pending live test with real test keys.
- 2026-07-12: Phase B VERIFIED — app runs on Neon PostgreSQL, all 5 migrations applied, data persists across restart (confirmed via API). Fixed broken ProductServiceImplTest; full suite green 10/10.
- 2026-07-12: Phase B (config) — added `prod` PostgreSQL profile (env-var driven), made active profile overridable via SPRING_PROFILES_ACTIVE, added `.env.example`. Migrations already Postgres-native. Pending: connect a real Postgres DB and verify persistence.
- 2026-07-12: Phase A complete — fixed checkout (real variant price, stock decrement + insufficient-stock guard), linked CartItem/OrderItem → ProductVariant (migration V5), seeded real variants with price+stock, added .gitignore + untracked target/. Clean-compiled with JDK 21.
- 2026-07-12: Plan file created; project audited.
