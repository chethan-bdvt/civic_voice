# Civic Voice

A production-oriented Spring Boot backend for anonymous corruption and bribery allegation reporting.

## Core safety model

- Reports are initially marked as `SUBMITTED`
- Public-facing status is limited to `UNVERIFIED_ALLEGATION`
- The platform does not publish a person as guilty
- Evidence is stored securely and not exposed publicly by default
- Public search is limited to aggregated trend and summary data

## Stack

- Java 21
- Spring Boot 3.3
- Spring Security
- Spring Data JPA
- PostgreSQL ready
- H2 for local development and tests
- Swagger OpenAPI

## Run locally

```bash
cd "c:\DS Practice\projects\civic_voice"
mvn spring-boot:run
```

Then open:

- http://localhost:8080/swagger-ui/index.html
- http://localhost:8080/h2-console

## Key endpoints

- POST `/api/reports`
- GET `/api/reports/search`
- GET `/api/reports/{id}`
- GET `/api/reports/summary`

## Suggested next steps

- add real authentication with JWT or OAuth2
- add moderation workflow and admin panel
- add encrypted object storage for evidence
- add Kafka for asynchronous moderation events
- move from H2 to PostgreSQL for production

## Note

This is a production-focused starter, not a legal opinion. Use it as a technical foundation and get legal review before public deployment.
