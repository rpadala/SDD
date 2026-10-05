# Brownfield Users App Architecture Baseline

## Document type

This is a current-state baseline for the repository at `/Users/raj/raj-code-ws/POC/GitHubSpecKit/brownfield-users-app`.

This document records only repository evidence. It is not a target-state constitution and it does not treat implementation patterns as standards.

## 1. Application overview

Directly observed facts:
- The project is a Java/Spring Boot application named `brownfield-users-app`.
- The application entry point is [src/main/java/com/example/users/UsersApplication.java](../../src/main/java/com/example/users/UsersApplication.java).
- The app exposes a set of user-management endpoints under `/api/auth` and `/api/users`.
- The app uses Spring Data JPA with an in-memory H2 database, Spring Security, validation, and Spring Boot Actuator.

Inferred characteristics:
- This appears to be a small CRUD-style user-management service with registration, password reset, and user listing capability.
- The app is structured as a single-module Spring Boot service rather than a multi-module monorepo.

Missing capabilities or gaps:
- No multi-environment deployment manifests or infrastructure-as-code files were found in the repository.
- No external service integrations beyond the local H2 database and the standard Spring Boot runtime stack were identified.

## 2. Technology stack and versions

Directly observed facts:
- Java version: 21 in [pom.xml](../../pom.xml).
- Spring Boot version: 4.1.0 via the parent POM in [pom.xml](../../pom.xml).
- Maven wrapper is present in [.mvn/wrapper/maven-wrapper.properties](../../.mvn/wrapper/maven-wrapper.properties) and points to Maven 3.9.16.
- Primary dependencies declared in [pom.xml](../../pom.xml):
  - `spring-boot-starter-webmvc`
  - `spring-boot-starter-data-jpa`
  - `spring-boot-starter-security`
  - `spring-boot-starter-validation`
  - `com.h2database:h2`
  - `spring-boot-starter-actuator`
  - `micrometer-registry-prometheus`
  - `spring-boot-starter-test`
  - `spring-boot-starter-webmvc-test`
  - `spring-security-test`

Observed configuration:
- [src/main/resources/application.properties](../../src/main/resources/application.properties) sets:
  - `spring.application.name=brownfield-users-app`
  - H2 in-memory JDBC URL: `jdbc:h2:mem:usersdb`
  - `spring.jpa.hibernate.ddl-auto=create-drop`
  - `spring.h2.console.enabled=true`
  - `spring.h2.console.path=/h2-console`
  - Actuator exposure includes `health,info,metrics,prometheus`

## 3. Project and module structure

Directly observed facts:
- The repository is a single Maven project with one root module.
- Source layout is:
  - `src/main/java/com/example/users/`
  - `src/main/resources/`
  - `src/test/java/com/example/users/`
- Java package structure includes:
  - `config`
  - `controller`
  - `dto`
  - `entity`
  - `repository`
  - `service`

Evidence:
- [src/main/java/com/example/users/UsersApplication.java](../../src/main/java/com/example/users/UsersApplication.java)
- [src/main/java/com/example/users/config/SecurityConfig.java](../../src/main/java/com/example/users/config/SecurityConfig.java)
- [src/main/java/com/example/users/controller/AuthController.java](../../src/main/java/com/example/users/controller/AuthController.java)
- [src/main/java/com/example/users/controller/UserController.java](../../src/main/java/com/example/users/controller/UserController.java)
- [src/main/java/com/example/users/entity/User.java](../../src/main/java/com/example/users/entity/User.java)
- [src/main/java/com/example/users/repository/UserRepository.java](../../src/main/java/com/example/users/repository/UserRepository.java)
- [src/main/java/com/example/users/service/UserService.java](../../src/main/java/com/example/users/service/UserService.java)

## 4. Package architecture and implementation components

Directly observed facts:

### Config
- [SecurityConfig.java](../../src/main/java/com/example/users/config/SecurityConfig.java) defines a `PasswordEncoder` bean using `BCryptPasswordEncoder`.
- It also defines a `SecurityFilterChain` with form login and permit-all rules.

### Controllers
- [AuthController.java](../../src/main/java/com/example/users/controller/AuthController.java):
  - `@RestController`
  - `@RequestMapping("/api/auth")`
  - POST `/api/auth/register`
  - POST `/api/auth/reset-password`
- [UserController.java](../../src/main/java/com/example/users/controller/UserController.java):
  - `@RestController`
  - `@RequestMapping("/api/users")`
  - GET `/api/users`

### Services
- [UserService.java](../../src/main/java/com/example/users/service/UserService.java):
  - registers new users
  - checks for duplicate email addresses
  - resets a user password
  - lists all users as `UserResponse`
  - uses constructor injection for `UserRepository` and `PasswordEncoder`

### Repositories
- [UserRepository.java](../../src/main/java/com/example/users/repository/UserRepository.java) extends `JpaRepository<User, Long>`.
- It defines:
  - `Optional<User> findByEmail(String email)`
  - `boolean existsByEmail(String email)`

### Entity and DTOs
- [User.java](../../src/main/java/com/example/users/entity/User.java) is a JPA entity mapped to table `users` and has fields for:
  - `id`
  - `firstName`
  - `lastName`
  - `email`
  - `password`
  - `address`
  - `dateOfBirth`
- The entity uses `@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))` and `@GeneratedValue(strategy = GenerationType.IDENTITY)`.
- DTO records are present in:
  - [RegistrationRequest.java](../../src/main/java/com/example/users/dto/RegistrationRequest.java)
  - [PasswordResetRequest.java](../../src/main/java/com/example/users/dto/PasswordResetRequest.java)
  - [LoginRequest.java](../../src/main/java/com/example/users/dto/LoginRequest.java)
  - [UserResponse.java](../../src/main/java/com/example/users/dto/UserResponse.java)

## 5. API endpoints and behavior

Directly observed facts:
- `POST /api/auth/register` accepts a `RegistrationRequest` body and returns a `UserResponse` with HTTP 201.
- `POST /api/auth/reset-password` accepts a `PasswordResetRequest` and responds with HTTP 204.
- `GET /api/users` returns a list of `UserResponse` objects.
- The Security configuration explicitly permits:
  - `/api/auth/register`
  - `/api/auth/reset-password`
  - `/h2-console/**`
  - `/actuator/health`
- All other requests require authentication.

Inferred characteristics:
- This is a secure-by-default web app with a form-based login flow and password encoding.
- The API is likely intended to be a minimal user-signup and profile management backend.

Uncertainty:
- There is no explicit API documentation file or OpenAPI contract in the repository, so the API surface is evidenced only from the controller mappings and tests.

## 6. Persistence, database, and migrations

Directly observed facts:
- The app uses Spring Data JPA and Hibernate via dependency declaration in [pom.xml](../../pom.xml).
- The database is configured in [application.properties](../../src/main/resources/application.properties) as an H2 in-memory database:
  - `jdbc:h2:mem:usersdb`
- `spring.jpa.hibernate.ddl-auto=create-drop` is set.
- The app exposes the H2 console at `/h2-console`.
- The entity is named `User`, and the table is `users`.

Missing capabilities or gaps:
- No Flyway migration scripts were found under the repository.
- No SQL schema files, migration directory, or external database configuration were found.
- No secondary database/tenant configuration or data-access abstraction beyond the single repository was found.

## 7. Security and authentication

Directly observed facts:
- `spring-boot-starter-security` is declared in [pom.xml](../../pom.xml).
- [SecurityConfig.java](../../src/main/java/com/example/users/config/SecurityConfig.java) disables CSRF.
- Form login is configured with:
  - `loginProcessingUrl("/api/auth/login")`
  - username parameter `email`
  - password parameter `password`
- The app uses a `PasswordEncoder` bean with `BCryptPasswordEncoder`.
- [CustomUserDetailsService.java](../../src/main/java/com/example/users/service/CustomUserDetailsService.java) implements `UserDetailsService` and loads a user by email.
- `SecurityConfig` permits registration, reset-password, H2 console, and actuator health endpoints.

Observed risk / caution:
- The app disables CSRF globally, which is consistent with a stateless or form-login API design but should be reviewed with the intended client behavior and deployment context.

## 8. Configuration and profiles

Directly observed facts:
- [src/main/resources/application.properties](../../src/main/resources/application.properties) is the only application config file found.
- It includes application name, H2 datasource settings, JPA config, H2 console config, and Actuator configuration.
- No `application-dev.properties`, `application-prod.properties`, or profile-specific configuration files were found.

Missing or not evidenced:
- No explicit separate environment profiles were found.
- No external configuration files or secret management integration were identified.

## 9. External integrations

Directly observed facts:
- The only external system directly configured in the project is the in-memory H2 database.
- The project does not declare client libraries for Redis, Kafka, SMTP, REST clients, or cloud services.
- Spring Boot Actuator is configured to expose metrics and Prometheus output.

Uncertainty:
- There is no additional deployment or environment configuration available in the repo to show whether the app is intended to talk to any external service.

## 10. Testing structure

Directly observed facts:
- Test classes found under [src/test/java/com/example/users](../../src/test/java/com/example/users):
  - [UserServiceTest.java](../../src/test/java/com/example/users/UserServiceTest.java)
  - [AuthControllerIntegrationTest.java](../../src/test/java/com/example/users/AuthControllerIntegrationTest.java)
  - [ObservabilityIntegrationTest.java](../../src/test/java/com/example/users/ObservabilityIntegrationTest.java)
- The tests include:
  - a service unit test for registration
  - an MVC integration test for registration endpoint behavior
  - an MVC integration test for `/actuator/health`

Fresh verification:
- Running `./mvnw test` in this repository completed successfully with:
  - `Tests run: 3`
  - `Failures: 0`
  - `Errors: 0`
  - `BUILD SUCCESS`

## 11. Build, CI/CD, and deployment information

Directly observed facts:
- The project uses Maven via [pom.xml](../../pom.xml).
- The Maven wrapper is present and configured in [.mvn/wrapper/maven-wrapper.properties](../../.mvn/wrapper/maven-wrapper.properties).
- CI workflow at [.github/workflows/ci.yaml](../../.github/workflows/ci.yaml) runs:
  - Java 21 setup
  - `./mvnw clean test`
  - `./mvnw package -DskipTests`
  - artifact upload of the built JAR

This indicates:
- The repo includes a GitHub Actions build pipeline for Java 21, Maven test execution, packaging, and artifact upload.

Not observed:
- No Dockerfile
- No Kubernetes manifests
- No Terraform or Bicep files
- No cloud deployment manifests
- No release automation beyond the GitHub Actions workflow

## 12. Observability and logging

Directly observed facts:
- The app includes Spring Boot Actuator and Micrometer Prometheus registry in [pom.xml](../../pom.xml).
- Metrics and health endpoints are exposed through the config in [application.properties](../../src/main/resources/application.properties):
  - `management.endpoints.web.exposure.include=health,info,metrics,prometheus`
  - `management.endpoint.health.show-details=when-authorized`
- [ObservabilityIntegrationTest.java](../../src/test/java/com/example/users/ObservabilityIntegrationTest.java) validates that `/actuator/health` returns HTTP 200 and JSON status `UP`.

Missing or not evidenced:
- No custom application logging statements or a dedicated structured logging configuration file were identified.
- No tracing or APM integration beyond the standard Actuator/Micrometer stack was found.

## 13. Architectural patterns actually observed

Directly observed patterns:
- Single-module Spring Boot application.
- Layered package structure: controller/service/repository/entity/dto/config.
- JPA-backed persistence model using an entity and repository.
- Spring Security with form login and password hashing.
- REST-style JSON API using Spring MVC controllers.
- Test-first service validation and MVC integration tests.

## 14. Deviation from constitution, if present

This repo contains a constitution template under [.specify/memory/constitution.md](../../.specify/memory/constitution.md), but it is still a placeholder and not an implemented project standard.

Because the repository constitution is not yet populated with actual project rules, there are no repository-evidenced constitution requirements to compare against beyond the general fact that the app is a Spring Boot Java project.

Directly observed differences to a typical constitution-driven stack:
- The repository does not include a custom deployment or environment policy file.
- There is no custom architecture governance file beyond the placeholder constitution template.
- There is no evidence of a defined versioning or release policy beyond GitHub Actions packaging.

## 15. Technical debt and risks

Directly observed risks and concerns:
- A global CSRF disable may be intentional but should be reviewed against the actual client behavior and threat model.
- The database is in-memory H2, which is suitable for local testing but is not a production-grade persistence choice unless explicitly intended.
- The app has no environment-specific configuration or secrets management setup evident in the repository.
- No migration framework is configured, which means schema changes are dependent on `create-drop` semantics rather than explicit migration files.
- The app exposes the H2 console when enabled; that requires careful review if deployed outside local development.

## 16. Evidence index

Key evidence files:
- [pom.xml](../../pom.xml)
- [src/main/resources/application.properties](../../src/main/resources/application.properties)
- [src/main/java/com/example/users/UsersApplication.java](../../src/main/java/com/example/users/UsersApplication.java)
- [src/main/java/com/example/users/config/SecurityConfig.java](../../src/main/java/com/example/users/config/SecurityConfig.java)
- [src/main/java/com/example/users/controller/AuthController.java](../../src/main/java/com/example/users/controller/AuthController.java)
- [src/main/java/com/example/users/controller/UserController.java](../../src/main/java/com/example/users/controller/UserController.java)
- [src/main/java/com/example/users/service/UserService.java](../../src/main/java/com/example/users/service/UserService.java)
- [src/main/java/com/example/users/service/CustomUserDetailsService.java](../../src/main/java/com/example/users/service/CustomUserDetailsService.java)
- [src/main/java/com/example/users/repository/UserRepository.java](../../src/main/java/com/example/users/repository/UserRepository.java)
- [src/main/java/com/example/users/entity/User.java](../../src/main/java/com/example/users/entity/User.java)
- [src/test/java/com/example/users/UserServiceTest.java](../../src/test/java/com/example/users/UserServiceTest.java)
- [src/test/java/com/example/users/AuthControllerIntegrationTest.java](../../src/test/java/com/example/users/AuthControllerIntegrationTest.java)
- [src/test/java/com/example/users/ObservabilityIntegrationTest.java](../../src/test/java/com/example/users/ObservabilityIntegrationTest.java)
- [.github/workflows/ci.yaml](../../.github/workflows/ci.yaml)
- [.mvn/wrapper/maven-wrapper.properties](../../.mvn/wrapper/maven-wrapper.properties)
