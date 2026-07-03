# AGENTS.md — Hyperwallet Mirakl Connector

## Stack

Java 25 + Spring Boot multi-module Gradle project. All convention plugins live in `buildSrc/`. A separate Cucumber-js/TypeScript BDD suite lives in `e2y/testing/bdd/` (not part of the Gradle build).

## Mandatory environment variables

The build will fail without these (private Mirakl Maven repository):

```bash
export PAYPAL_MIRAKL_SDK_USER=<value>
export PAYPAL_MIRAKL_SDK_PASSWORD=<value>
```

Running the connector also requires `PAYPAL_HYPERWALLET_*` and `PAYPAL_MIRAKL_*` env vars — see `hmc-app/src/main/resources/application.properties` for the full list.

## Key commands

```bash
# Clean build (runs format automatically before compile)
./gradlew clean build

# Build without tests (faster CI-style check)
./gradlew build -x test -x integrationTest

# Unit tests only
./gradlew test

# Integration tests only (run after unit tests)
./gradlew integrationTest

# Full check + coverage (what CI runs)
./gradlew check jacocoTestReport --continue

# Reformat source files (Spring Java Format / Eclipse JDT style)
./gradlew format

# Checkstyle (main sources only — test sources are excluded)
./gradlew checkstyle

# Run a single test class in a module
./gradlew hmc-sellers:test --tests "com.paypal.sellers.sellerextractioncommons.model.SellerModelTest"

# SonarCloud analysis (requires SONAR_TOKEN)
./gradlew sonar -Dsonar.organization=e2y -Dsonar.projectKey=paypal-hyperwallet

# Build Docker Compose file + OCI image
./gradlew buildDockerCompose
docker-compose up
```

## Toolchain quirks

- **Auto-format on compile**: `compileJava` depends on `format`. Any compile task rewrites source files in-place using Spring Java Format (Eclipse JDT-based — not Google Java Format). Expect modified files after `./gradlew build`.
- **Git-based versioning**: Version comes from `com.palantir.gradle.gitversion`. Shallow clones produce wrong versions. CI uses `fetch-depth: 0`.
- **Checkstyle applies to main sources only**: `checkstyleTest.enabled = false`.
- **`bootRun` forces UTC**: JVM timezone set to `UTC` via `bootRun` JVM args. Mirror this in manual test setups involving date/time.
- **`integrationTest` source set** lives at `src/integrationTest/java/` — a separate Gradle test suite, not an annotation filter. Not every module has one.
- **ArchUnit tests**: Architecture fitness tests are in the test suite and will fail the build if package dependency rules are violated.
- **AspectJ weaving in `hmc-observability`**: Uses `inpath` to weave aspects into Hyperwallet and Mirakl SDK bytecode directly (unusual; powered by `io.freefair.aspectj`).
- **Lombok + MapStruct**: Both annotation processors configured correctly in convention plugins; do not add them manually to individual modules.
- **Paketo Buildpack file permissions**: The OCI image runs as user `cnb` (UID `1002:1001`) with workdir `/workspace`. The `cnb` user cannot create directories under `/workspace`. Set `PAYPAL_HYPERWALLET_DATA_DIR=/tmp/data` for test/CI environments (no volume needed — `/tmp` is world-writable inside the container). For production use a named volume with an `init-data-dir` service that chowns it to `1002:1001` before the app starts.
- **Docker bind mounts and ownership**: Any bind mount (`./host/path:/container/path`) where the host directory does not exist will be created by Docker as `root:root`, making it unwritable by non-root container users. Either pre-create the host directory with the correct owner, or prefer named volumes with an init container for automatic ownership handling.

## Module map

| Module | Purpose |
|---|---|
| `hmc-app` | Spring Boot entrypoint; produces the executable JAR (`bootJar`) |
| `hmc-infrastructure` | Shared infra: JPA, HTTP clients, config, cross-cutting concerns |
| `hmc-jobsystem` | Quartz-based batch job scheduling |
| `hmc-sellers` | Seller onboarding: Mirakl → Hyperwallet |
| `hmc-invoices` | Invoice/payout processing |
| `hmc-kyc` | KYC verification workflow |
| `hmc-notifications` | Email alerts |
| `hmc-reports` | Reporting |
| `hmc-observability` | Observability (AspectJ weaving into SDKs) |
| `hmc-testsupport` | Shared test utilities (MockServer, ArchUnit helpers) — test scope only |

Only `hmc-app` produces a runnable JAR. All other modules have `bootJar { enabled = false }`.

## Required ordering

1. Tests before integration tests: handled automatically by Gradle (`shouldRunAfter`).
2. Jacoco report after tests: `jacocoTestReport` depends on both `test` and `integrationTest`. Running it standalone without prior test execution produces empty/missing reports.
3. Docker: `./gradlew build` → `./gradlew buildDockerCompose` → `docker-compose up`.

## BDD / E2E tests (`e2y/testing/bdd/`)

Node.js 18 LTS required. Not wired into Gradle.

```bash
cd e2y/testing/bdd
yarn install
yarn test                         # all scenarios
yarn test --tags "@scenario_tag"  # filter by tag
npm run report                    # generate HTML report
```

Mirakl OpenAPI TypeScript schemas must be placed in `resources/mirakl/` (downloaded from `help.mirakl.net`, converted with `npx openapi-typescript`). CI fetches these from an AWS S3 bucket (`e2y-mirakl-schemas`).

## Husky pre-commit hook

Runs only when `e2y/testing/bdd/` files are staged: `yarn tsc` + `lint-staged`. Stripped before publishing to the public repo.

## Dual-repo setup

This is the private repo. A `publish-on-public-repo.yml` workflow syncs to a public GitHub repo, excluding `e2y/`, `.husky/`, and `.env`. Do not commit internal-only material outside those paths.
