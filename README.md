# Stateless Security

Backend demo for stateless Spring Security using JWT, role/privilege checks, Google login, refresh tokens, and optional TOTP 2FA.

## Quick Start
```bash
./gradlew bootRun
```

- Base URL: `http://localhost:8800`
- Swagger UI: `http://localhost:8800/swagger-ui.html`
- DB: H2 in-memory (`jdbc:h2:mem:stateless_security`)

## Security (Implemented)
- Stateless API security (`SessionCreationPolicy.STATELESS`)
- RSA-signed JWT access and refresh tokens
- Username/password login: `POST /authenticate`
- OAuth2 password token endpoint: `POST /oauth/token`
- Refresh token endpoint: `POST /refresh-token`
- Google login: `POST /authenticateGoogle`
- Optional TOTP 2FA (`/api/2fa/setup`, `/api/2fa/activate`, `/api/2fa/disable`)
- Dynamic ACL check per URL + HTTP method via DB mappings
- Token invalidation for all devices: `POST /api/user/logout-all-devices`

## Public Endpoints
- `POST /authenticate`
- `POST /authenticateGoogle`
- `POST /oauth/token`
- `POST /register`

## Swagger Usage
1. Open `http://localhost:8800/swagger-ui.html`
2. Get token:
   - Use `POST /authenticate` (JSON) or
   - Use OAuth2 password flow (`/oauth/token`) from Swagger auth
3. Click **Authorize** and provide bearer token
4. Call secured `/api/**` endpoints

## Notes
- Replace `src/main/resources/certs/private.pem` and `src/main/resources/certs/public.pem` outside demo use.
- Configure Google credentials in `src/main/resources/application.yml` (`app.google.client-id`, `app.google.client-secret`).
