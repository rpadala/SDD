# User Management Migration Plan

**Feature**: User Management
**Status**: migrated
**Source of truth**: Repository implementation and tests

## Technical Context

The current implementation is a single-module Spring Boot 4.1.0 application with Java 21 and Maven.

### Observed architecture

- Application entry point: [UsersApplication.java](../../src/main/java/com/example/users/UsersApplication.java)
- Package structure: `config`, `controller`, `dto`, `entity`, `repository`, `service`
- Controller layer: [AuthController.java](../../src/main/java/com/example/users/controller/AuthController.java) and [UserController.java](../../src/main/java/com/example/users/controller/UserController.java)
- Service layer: [UserService.java](../../src/main/java/com/example/users/service/UserService.java)
- Persistence layer: [UserRepository.java](../../src/main/java/com/example/users/repository/UserRepository.java) and [User.java](../../src/main/java/com/example/users/entity/User.java)
- Security layer: [SecurityConfig.java](../../src/main/java/com/example/users/config/SecurityConfig.java)
- Authentication user lookup: [CustomUserDetailsService.java](../../src/main/java/com/example/users/service/CustomUserDetailsService.java)

### Runtime and dependency details

- Framework: Spring MVC + Spring Security + Spring Data JPA
- Database: H2 in-memory database configured in [application.properties](../../src/main/resources/application.properties)
- Validation: `spring-boot-starter-validation`
- Actuator: enabled for health and metrics
- Tests: JUnit 5, MockMvc, Mockito, Spring Boot test support

## Existing Implementation Phases

### Phase 1 - User model and persistence

- A JPA `User` entity was added with email uniqueness and required fields.
- A repository layer was added to persist and query user records by email.
- The database is configured as an embedded H2 target with schema generation via Hibernate.

### Phase 2 - Registration and authentication features

- DTO records were implemented for registration, login, password reset, and responses.
- `UserService` implements registration, duplicate-email checks, password encoding, and password resets.
- Authentication uses `UserDetailsService` and a BCrypt `PasswordEncoder`.

### Phase 3 - HTTP API surface

- The auth controller exposes registration and reset-password endpoints.
- The user controller exposes a user list endpoint.
- Security rules allow public auth endpoints and health checks while requiring authentication elsewhere.

### Phase 4 - Testing and observability

- Service and MVC integration tests validate registration and health endpoints.
- Actuator configuration exposes health and Prometheus-style metrics.

## Key Decisions and Constraints

- The feature was implemented using Spring Boot defaults and a standard layered service design.
- The live repo does not define formal migration scripts or a production database strategy.
- The repo does not provide detailed business workflows beyond registration, reset-password, and user listing.

## Risks and Technical Debt

- H2 is used in-memory, which is suitable for current local/test behavior but not a production persistence strategy.
- No database migration framework is configured in the repository.
- CSRF is disabled in the security config and should be reviewed by humans for the intended client architecture.
- The constitution file remains a placeholder and does not provide project-specific engineering standards for this feature.

## Current Architecture Summary

This is a single Spring Boot web application with layered MVC and JPA patterns. The feature is already implemented and validated by tests. The migration captures the current implementation without inventing new requirements or converting observed patterns into mandatory standards.
