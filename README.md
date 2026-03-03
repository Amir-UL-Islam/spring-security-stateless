# Stateless Security

Stateless Security is a Spring Boot 4 + React (TypeScript) demo that shows how to build a fully stateless authentication and authorization layer with JWT, role-based access control, and a small CRUD admin UI. The backend exposes REST endpoints secured with bearer tokens; the frontend bundles into the Spring app and consumes those APIs.

## Architecture
- Spring Boot 4 (Java 25), Spring Security with a stateless JWT filter chain, Spring Data JPA, H2 in-memory database.
- Frontend built with React 19, React Router 7, React Hook Form, Bootstrap 5; bundled via Webpack and served as static assets by Spring.
- OpenAPI via springdoc; error responses unified through `error-handling-spring-boot-starter`.
- Google social login flow exchanges an OAuth code for an ID token, validates it against the configured client, and issues a local JWT.

## Features
- Email/password login issuing JWT access tokens at `/authenticate`.
- Social login for Google at `/authenticateGoogle` using OAuth code + PKCE style redirect back to `/completeLogin`.
- Role and privilege management: CRUD endpoints for users, roles, privileges, and URL-to-role mappings under `/api/**` (see `src/main/java/io/security/base`).
- Initial data seeding of `ADMIN` and `USER` roles via `RoleLoader`.
- React admin UI with protected routes for managing users, roles, privileges, and URL rules.

## Prerequisites
- Java 25
- Node is downloaded automatically by Gradle (`com.github.node-gradle.node`); a system Node/npm is optional.

## Running locally
1. Build frontend + backend and start the app: `./gradlew bootRun`
2. The server listens on `http://localhost:8800` (see `src/main/resources/application.yml`).
3. H2 runs in-memory (`jdbc:h2:mem:stateless-security`); data resets on restart.
4. Swagger UI (OpenAPI docs) is available at `/swagger-ui.html` once the app is running.

### Authentication setup
- JWT signing keys are provided in `src/main/resources/certs`. Replace them for real deployments.
- Google OAuth: set `app.google.client-id` and `app.google.client-secret` in `src/main/resources/application.yml` (or env/profiles). The frontend expects redirect `http://localhost:8800/completeLogin?provider=google` during local dev.

## Frontend
- Entry point `src/main/webapp/index.tsx`; routes defined in `src/main/webapp/app/routes.tsx`.
- Protected pages enforce required roles (e.g., ADMIN-only for role/privilege/URL management).
- Development-only React server: `npm run devserver` (uses webpack-dev-server). Production build is triggered automatically by Gradle.

## API snapshot
- Auth: `POST /authenticate`, `POST /authenticateGoogle`, `POST /register` (self-registration with hashed password).
- Users: `GET/POST/PUT/DELETE /api/user` with pagination and role lookups via `/api/user/roleValues`.
- Roles: `GET/POST/PUT/DELETE /api/roles`.
- Privileges: `GET/POST/PUT/DELETE /api/privileges`.
- URL rules: `GET/POST/PUT/DELETE /api/urls` to map endpoints to required roles.
- All `/api/**` routes require JWT bearer auth; method access is gated by `@PreAuthorize` with `ADMIN`/`USER` roles.

## Testing
- Backend tests: `./gradlew test` (JUnit + RestAssured + Spring Modulith test starter).
- Frontend tests: `npm test` (Jest + React Testing Library). Gradle does not invoke them automatically; run manually when needed.

## Deployment notes
- App is stateless; any node can serve traffic as long as it has the signing keys. Use an external database for persistence in non-demo environments.
- Configure a real OAuth consent screen and secure TLS endpoints before enabling Google login in production.
