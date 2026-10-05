# Brownfield Validation Report

## 1. Validation summary

This validation checks whether the brownfield Spec Kit configuration and supporting documents accurately reflect the live repository state for `/Users/raj/raj-code-ws/POC/GitHubSpecKit/brownfield-users-app`.

The validation used the repository as the authoritative source of truth and cross-checked it against:

- [.specify/memory/application-architecture-baseline.md](application-architecture-baseline.md)
- [.specify/memory/brownfield-bootstrap-report.md](brownfield-bootstrap-report.md)
- [.specify/memory/constitution.md](constitution.md)
- the current Spec Kit workflow and templates under [.specify](../)
- the application source, tests, Maven build, CI workflow, and runtime config

The overall result is: PASS WITH WARNINGS.

Reason: the repository state is internally consistent with the baseline and bootstrap report, but some target-state and implementation-detail areas remain intentionally incomplete or not yet fully specified.

## 2. Build/test verification

Fresh verification command executed:

- `cd /Users/raj/raj-code-ws/POC/GitHubSpecKit/brownfield-users-app && ./mvnw test`

Fresh result:

- `Tests run: 3`
- `Failures: 0`
- `Errors: 0`
- `BUILD SUCCESS`

This confirms the project currently builds and tests successfully under the repository’s own Maven wrapper.

## 3. Architecture baseline validation

Validation target: [.specify/memory/application-architecture-baseline.md](application-architecture-baseline.md)

### Assessment

The baseline is largely consistent with the repository evidence. It correctly identifies the project as a Java 21 Spring Boot 4.1.0 service with Spring MVC, JPA, Security, validation, H2, and Actuator.

Validated areas:

- Java version is 21 in [pom.xml](../../pom.xml)
- Spring Boot version is 4.1.0 in [pom.xml](../../pom.xml)
- H2 datasource configuration matches [src/main/resources/application.properties](../../src/main/resources/application.properties)
- JPA entity/repository structure matches [User.java](../../src/main/java/com/example/users/entity/User.java) and [UserRepository.java](../../src/main/java/com/example/users/repository/UserRepository.java)
- Security config matches [SecurityConfig.java](../../src/main/java/com/example/users/config/SecurityConfig.java)
- API mappings match [AuthController.java](../../src/main/java/com/example/users/controller/AuthController.java) and [UserController.java](../../src/main/java/com/example/users/controller/UserController.java)
- Observability config aligns with [application.properties](../../src/main/resources/application.properties) and [ObservabilityIntegrationTest.java](../../src/test/java/com/example/users/ObservabilityIntegrationTest.java)

Minor caveat:

- The baseline describes the repo as a general API and a small user-signup service; the route names and DTOs support that conclusion, but the exact business domain beyond registration/password reset is still inferred rather than formally specified elsewhere.

## 4. Bootstrap validation

Validation target: [.specify/memory/brownfield-bootstrap-report.md](brownfield-bootstrap-report.md)

### Assessment

The bootstrap report is consistent with the repository evidence and the architecture baseline.

Validated points:

- The app is a single-module Spring Boot project, as the report states.
- The baseline used by the report matches the repository state.
- The report correctly records that no application source, tests, build files, or config files were modified during bootstrap.
- The report accurately distinguishes current-state evidence from target-state standards.

No direct conflict was found between the bootstrap report and the live repository state.

## 5. Application capability validation

### Observed application capabilities

From repository evidence:

- User registration via `POST /api/auth/register`
- Password reset via `POST /api/auth/reset-password`
- User listing via `GET /api/users`
- Spring Security form-login configuration with BCrypt password encoding
- JPA-backed in-memory persistence using H2
- Actuator health and metrics endpoints

Evidence:

- [AuthController.java](../../src/main/java/com/example/users/controller/AuthController.java)
- [UserController.java](../../src/main/java/com/example/users/controller/UserController.java)
- [UserService.java](../../src/main/java/com/example/users/service/UserService.java)
- [SecurityConfig.java](../../src/main/java/com/example/users/config/SecurityConfig.java)
- [application.properties](../../src/main/resources/application.properties)

### Capability conclusion

The application capability summary in the baseline is accurate for the current repository state. It is not over-claiming broader business scope than what the code shows.

## 6. CI/CD validation

Validation target: [.github/workflows/ci.yaml](../../.github/workflows/ci.yaml)

### Assessment

The CI workflow is consistent with the repository and with the architecture baseline.

Observed behavior:

- Java 21 is set up in CI.
- Maven wrapper is made executable.
- `./mvnw clean test` runs.
- `./mvnw package -DskipTests` packages the app.
- The JAR artifact is uploaded as a workflow artifact.

This matches the actual project build and test state.

Classification: VALID

## 7. Observability validation

Validation target: [application.properties](../../src/main/resources/application.properties)

### Assessment

The observability setup is correctly described in the baseline and bootstrap report:

- Actuator is present via dependency in [pom.xml](../../pom.xml)
- Metrics and Prometheus are enabled in [application.properties](../../src/main/resources/application.properties)
- Health endpoint is validated in [ObservabilityIntegrationTest.java](../../src/test/java/com/example/users/ObservabilityIntegrationTest.java)

Observed endpoints:

- `/actuator/health`
- Prometheus metrics exposure configured by the application properties

Classification: VALID

## 8. Security validation

Validation targets:

- [SecurityConfig.java](../../src/main/java/com/example/users/config/SecurityConfig.java)
- [CustomUserDetailsService.java](../../src/main/java/com/example/users/service/CustomUserDetailsService.java)
- [application.properties](../../src/main/resources/application.properties)

### Assessment

The project has a real security configuration with:

- Spring Security dependency in [pom.xml](../../pom.xml)
- BCrypt password encoder
- form login for `/api/auth/login`
- route allowlists for auth and health endpoints
- authenticated access to all other endpoints

This is accurately reflected in the architecture baseline.

Potential review item:

- `csrf` is disabled globally. That may be intentional for this app’s API flow, but it should be reviewed against the actual clients and deployment context.

Classification: VALID with a human-review warning.

## 9. Testing validation

Validation targets:

- [UserServiceTest.java](../../src/test/java/com/example/users/UserServiceTest.java)
- [AuthControllerIntegrationTest.java](../../src/test/java/com/example/users/AuthControllerIntegrationTest.java)
- [ObservabilityIntegrationTest.java](../../src/test/java/com/example/users/ObservabilityIntegrationTest.java)

### Assessment

The repository contains meaningful tests for:

- user registration service logic
- auth registration endpoint behavior
- health endpoint behavior

The test suite currently passes.

Classification: VALID

## 10. Current-state vs target-state validation

Validation targets:

- repository source and config as the authoritative current state
- [.specify/memory/application-architecture-baseline.md](application-architecture-baseline.md) as current-state evidence
- [.specify/memory/constitution.md](constitution.md) as target-state standards

### Assessment

The separation is correctly maintained:

- The repository plus the baseline represent current implementation.
- The constitution remains a target standard template rather than a description of the live codebase.

This distinction is important because the live repo already contains JPA, Spring Security, Actuator, H2, and tests, while the constitution template itself does not describe those as established repository behavior.

Classification: VALID

## 11. Findings table

| Finding | Classification | Repository evidence | Affected artifact | Recommended action |
|---|---|---|---|---|
| Java 21 + Spring Boot 4.1.0 project is correctly identified | VALID | [pom.xml](../../pom.xml) | Architecture baseline | Keep as-is |
| H2 in-memory datasource configuration is correctly identified | VALID | [application.properties](../../src/main/resources/application.properties) | Architecture baseline | Keep as-is |
| JPA entity/repository structure is correctly observed | VALID | [User.java](../../src/main/java/com/example/users/entity/User.java), [UserRepository.java](../../src/main/java/com/example/users/repository/UserRepository.java) | Architecture baseline | Keep as-is |
| Security setup is correctly described | VALID | [SecurityConfig.java](../../src/main/java/com/example/users/config/SecurityConfig.java) | Architecture baseline | Keep as-is |
| Actuator health and metrics exposure is correctly described | VALID | [application.properties](../../src/main/resources/application.properties), [ObservabilityIntegrationTest.java](../../src/test/java/com/example/users/ObservabilityIntegrationTest.java) | Architecture baseline | Keep as-is |
| Bootstrap report is consistent with repo evidence | VALID | [brownfield-bootstrap-report.md](brownfield-bootstrap-report.md) | Bootstrap report | Keep as-is |
| Current-state vs target-state separation is maintained | VALID | [constitution.md](constitution.md), [application-architecture-baseline.md](application-architecture-baseline.md) | Current Spec Kit setup | Keep as-is |
| Project-specific target-state standards are not yet defined in the constitution | MISSING | [constitution.md](constitution.md) | Constitution | Review whether the team wants a concrete project constitution beyond the placeholder |
| Production deployment manifests are not present | MISSING | Repository root and config files | Architecture baseline | Document this as a current limitation if the app is expected to be deployed beyond local/dev usage |
| Explicit external integration inventory beyond H2 and standard Spring stack is absent | MISSING | [pom.xml](../../pom.xml) and source tree | Architecture baseline | Document as currently not evidenced |
| Caution around global CSRF disable | UNCERTAIN | [SecurityConfig.java](../../src/main/java/com/example/users/config/SecurityConfig.java) | Security validation | Human review before broader API adoption |
| The exact business scope beyond user registration/reset is inferred rather than explicitly specified | UNCERTAIN | Controller mappings and DTOs | Architecture baseline | Keep as a noted assumption unless a formal domain spec is added |

## 12. Stale or conflicting information

No direct stale or conflicting information was found in the current brownfield repo artifacts.

The repository evidence and the saved baseline/bootstrap files align. The repository’s current state is consistent with the actual implementation and the build/test output.

## 13. Missing information

The following details are not presently evidenced in the repository and therefore remain missing from the current-state documentation:

- A project-specific target-state constitution beyond the placeholder template
- Formal deployment manifests or environment-specific configuration
- Explicit external integration contracts beyond H2 and standard Spring Boot infrastructure
- A detailed business-logic specification beyond the user-registration and password-reset API surface
- A more detailed production security model and environment review

## 14. Uncertainties

- The actual production deployment target is not defined in the repository.
- The intended client architecture for the security flow is not specified in repo artifacts.
- The repo does not state whether H2 is intended for local development only or for a future production replacement.
- There is no explicit domain model specification beyond the app’s current controller and service behavior.

## 15. Human-review items

The following items should be reviewed by a human before accepting the brownfield configuration as final:

- Whether the CSRF disable is intentional for the current client/server flow.
- Whether the in-memory H2 database is acceptable as a current-state baseline or whether it is only a development placeholder.
- Whether the placeholder constitution should be populated with actual project standards, or left intentionally generic until the team clarifies them.
- Whether additional deployment and environment configuration are expected but intentionally omitted from the repo.
- Whether the app scope and business rules should be documented explicitly beyond the current controller and DTO surface.

## 16. Final validation result

Final result: PASS WITH WARNINGS

Reason:

- The repository is internally consistent with the saved baseline and bootstrap report.
- The build passes with fresh evidence.
- The current-state documentation is aligned with the live implementation.
- The remaining warnings are about incomplete target-state specification, absent deployment metadata, and human review of security and environment choices — not about a broken or inaccurate baseline.
