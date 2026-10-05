# User Management Feature Specification

**Feature**: User Management
**Status**: migrated
**Created**: 2026-10-05
**Source of truth**: Repository implementation and automated tests

## Summary

This feature was reverse-engineered from the current repository implementation. It represents the live User Management behavior evidenced by the application source, repository configuration, and passing tests. It is not a design for a new feature.

## User Scenarios and Testing

### User Story 1 - Register a new account (Priority: P1)

A new user submits registration details to create an account.

**Why this priority**: This is the primary user-facing capability and is explicitly implemented in the application.

**Independent Test**: Auth registration endpoint accepts valid JSON and returns the created user payload without leaking the password.

**Acceptance Scenarios**:
1. Given a valid registration payload, when the user calls `POST /api/auth/register`, then the user is created and a 201 response is returned.
2. Given an email already in use, when the same registration request is submitted, then the service rejects it and raises a duplicate-email error.

---

### User Story 2 - Sign in through the application security flow (Priority: P1)

A user authenticates using their email and password through the configured Spring Security form-login flow.

**Why this priority**: The security configuration enables authentication for all non-public routes.

**Independent Test**: The application config wires a `UserDetailsService` and `PasswordEncoder` to the authentication manager.

**Acceptance Scenarios**:
1. Given a registered user, when the security flow processes `email` and `password`, then the user is authenticated using their persisted password hash.
2. Given a request for a protected endpoint without authentication, then the request is rejected.

---

### User Story 3 - Reset a password (Priority: P2)

A user requests a password reset using their email and a new password.

**Why this priority**: This is a clearly implemented workflow in the application and is exposed through the auth controller.

**Independent Test**: `POST /api/auth/reset-password` succeeds when the email exists and updates the stored password hash.

**Acceptance Scenarios**:
1. Given an existing user email, when a valid password reset request is submitted, then the password is replaced with a new encoded value.
2. Given an unknown email, when a reset request is submitted, then the application throws a user-not-found error.

---

### User Story 4 - List users (Priority: P3)

An authenticated user can retrieve the user list.

**Why this priority**: This is implemented but is a secondary capability compared to registration and authentication.

**Independent Test**: `GET /api/users` returns a list of sanitized user responses.

**Acceptance Scenarios**:
1. Given an authenticated user, when `GET /api/users` is called, then a list of users is returned.
2. Given an unauthenticated caller, when `GET /api/users` is called, then access is denied.

---

### Edge Cases

- Duplicate registration for the same email is rejected.
- Password reset for a non-existent user is rejected.
- Registration payloads with missing/invalid values are rejected by validation annotations.
- Password values are not returned in the user response payload.

## Requirements

### Functional Requirements

- **FR-001**: The application MUST allow a new user to register using first name, last name, email, password, optional address, and optional date of birth.
- **FR-002**: The application MUST enforce uniqueness of user email addresses.
- **FR-003**: The application MUST hash user passwords before storing them.
- **FR-004**: The application MUST allow an authenticated caller to retrieve the list of users.
- **FR-005**: The application MUST allow a user to reset their password using their email address and a new password.
- **FR-006**: The application MUST reject invalid or blank registration fields using validation annotations.
- **FR-007**: The application MUST expose the auth registration and password-reset operations through the REST API.

### Existing System Impact

- The existing system already contains a user registration and password reset flow.
- The live implementation persists user data in an H2 in-memory database and exposes a basic user-management API.
- This reverse-engineered spec describes current behavior; it does not redefine the feature beyond what the repository shows.

### Persistence and Migration Requirements

- Persisted user data is stored in the `users` table through JPA.
- The schema is created using Hibernate `ddl-auto=create-drop`; no Flyway migration is present.
- Existing behavior does not include an explicit migration backlog or historical data migration workflow.

## Success Criteria

### Measurable Outcomes

- **SC-001**: A valid registration request yields an HTTP 201 response and creates a user record.
- **SC-002**: An email duplicate causes the registration flow to fail deterministically.
- **SC-003**: A password reset request updates the stored password hash without returning it in the response.
- **SC-004**: A protected user listing endpoint returns user records only to authenticated callers.
- **SC-005**: The repository test suite for the feature passes successfully.

## Assumptions and Constraints

- The feature uses an in-memory H2 database for current repository behavior.
- The user model is represented by the `User` JPA entity and associated repository.
- Authentication is implemented using Spring Security form login plus a custom `UserDetailsService`.
- Unknown or undocumented business workflows such as email verification, password reset tokens, or multi-factor authentication remain unknown from repository evidence.

## Observed Deviations from Constitution

- The constitution is a placeholder and not a project-implemented standard for the live app.
- The live repo does not show a separate Flyway migration strategy; the current behavior is schema generation via Hibernate.
- The current repo does not provide a production-grade user-management policy document beyond the implemented code and tests.
