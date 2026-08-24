# JobPlacementApp

A job placement platform built as a set of Spring Boot microservices in Kotlin, fronted by an API gateway and a React single-page application, with Keycloak for authentication, Kafka for event streaming and a Prometheus/Grafana/Loki stack for monitoring. The whole system runs from one Docker Compose file.

Written for the Web Applications II course at Politecnico di Torino.

## Services

| Service | Port | Role |
| --- | --- | --- |
| `API_GW_service` | 8088 | Spring Cloud Gateway. OAuth2 login against Keycloak, token relay and circuit breakers on the downstream routes. |
| `CRM_service` | 8081 | The domain service: contacts, customers, professionals, job offers and messages. PostgreSQL and Kafka producer. |
| `DS_service` | 8082 | Document store: uploads documents with their metadata and serves them back. PostgreSQL. |
| `CM_service` | 8083 | Communication manager. Apache Camel routes that read Gmail, forward incoming mail to the CRM as messages, and send outgoing mail. |
| `AN_service` | 8086 | Analytics. Kafka consumer. |
| `spa` | 5173 | React, TypeScript and Vite frontend, served by Vite in dev mode. |

Each service is its own Gradle project with its own wrapper. The root `settings.gradle.kts` includes every subdirectory that has a build file, except `spa`. All services target Java 21 and expose Actuator health, info, metrics and Prometheus endpoints.

## Domain model

`CRM_service` is built around a `Contact`, which carries a name, surname, SSN code, category and any number of emails, addresses and phone numbers. A contact can be specialised as:

- **Customer** — owns job offers and can carry free-text notes.
- **Professional** — has a location, a daily rate, a set of skills, an employment state (`EMPLOYED`, `UNEMPLOYED`, `NOT_AVAILABLE`) and notes.

A **JobOffer** links a customer to an optional professional and holds a description, duration, value, profit margin, required skills and a status that moves through `CREATED`, `SELECTION_PHASE`, `CANDIDATE_PROPOSAL`, `CONSOLIDATED`, `DONE` and `ABORTED`. Invalid transitions raise a dedicated exception.

A **Message** arrives on one of three channels (`EMAIL`, `CALL`, `TEXT`) and moves through `RECEIVED`, `READ`, `PROCESSING`, `DONE`, `DISCARDED` and `FAILED`. Every transition is recorded as an `Action`, giving each message a history.

The schema is in `CRM_service/src/main/resources/schema.sql`; Hibernate is configured with `ddl-auto: update`.

## API

The gateway exposes the services under `/api/v1/**`, strips the two-segment prefix and relays the access token. It also serves `/me`, which returns the current principal along with login and logout URLs and the CSRF token, and redirects `/` and `/login/**` to `/ui`.

Behind the gateway:

| Prefix | Service | Resources |
| --- | --- | --- |
| `/API/contacts` | CRM | List and filter contacts, create one, add or change emails, addresses, phones, name, surname, SSN code and category |
| `/API/customers` | CRM | List and create customers, attach notes and job offers |
| `/API/professionals` | CRM | List and create professionals, manage skills and notes, change employment state |
| `/API/jobOffers` | CRM | Create and list offers, filter by open, accepted or aborted, manage skills, change status, read status and computed value |
| `/API/messages` | CRM | List, create and read messages, change state and priority, read the action history |
| `/API/documents` | DS | List documents, read metadata, read raw bytes, upload |
| `/API/emails` | CM | Send, list and delete mail |

Errors are returned as RFC 7807 problem details.

## Authentication

Keycloak runs the `API_Gateway` realm, imported at startup from `imports/realm-export.json`. It defines two roles, `guest` and `operator`, two users with those roles, and the clients used by the gateway (`API_GatewayClient`), the Camel routes (`camel-service`) and Prometheus.

The gateway performs the authorization-code flow and relays the resulting token; the other services validate it as OAuth2 resource servers. Controllers guard their methods with `@PreAuthorize`, where reads generally accept `ROLE_GUEST` or `ROLE_OPERATOR` and writes require `ROLE_OPERATOR`.

## Messaging and analytics

`CM_service` uses Apache Camel. `AutoGetEmailsRoute` fetches a service-account token from Keycloak, then streams unread Gmail messages, extracts sender, recipient and subject, and posts each one to the CRM as a new message. The remaining routes send, retrieve and delete mail on request.

`CRM_service` produces to Kafka and `AN_service` consumes. Debezium connectors registered by `load-kafka-connectors.sh` also stream the CRM `message` table and the DS database into Kafka topics as change events. `kafka-ui` on port 9090 exposes the broker.

## Monitoring

Compose brings up Prometheus (9091), Grafana (3000), Loki (3100), Promtail, node-exporter (9100), cAdvisor (8089) and a JMX exporter for Kafka (8087). Prometheus scrapes the Actuator endpoints of the CRM, DS, CM and AN services, plus Keycloak, Kafka and the exporters; the gateway's scrape job is present but commented out. Promtail ships container logs to Loki, and Grafana is provisioned with both Prometheus and Loki as data sources. Configuration lives in `prometheus-config.yaml`, `loki-config.yaml`, `promtail-config.yaml` and `grafana-datasources.yaml`.

## Running

Requires Docker and a JDK 21.

1. Fill in `.env` at the repository root:

   ```
   PROJECT_PATH        absolute path to this repository, used for bind mounts
   GMAIL_APP_NAME      Google Cloud project and OAuth credentials for the
   GMAIL_CLIENT_ID     Gmail account the CM service reads
   GMAIL_CLIENT_SECRET
   GMAIL_REFRESH_TOKEN
   CAMEL_CLIENT_SECRET      Keycloak client secrets, matching realm-export.json
   API_GW_CLIENT_SECRET
   ```

2. Build the service images. Each service configures `bootBuildImage` with the tag Compose expects, for example `com.example/crm_service:0.0.1-snapshot`:

   ```bash
   ./gradlew :CRM_service:bootBuildImage :DS_service:bootBuildImage :CM_service:bootBuildImage :AN_service:bootBuildImage :API_GW_service:bootBuildImage
   ```

3. Start everything:

   ```bash
   docker compose up
   ```

The frontend image is built from `spa/Dockerfile` by Compose itself. Once the stack is up, the application is reached through the gateway at `http://localhost:8088`, which redirects to the SPA. Keycloak's admin console is at `http://localhost:8080` with `admin` / `password`.

## Repository layout

```
API_GW_service/     Gateway
CRM_service/        Domain service
DS_service/         Document store
CM_service/         Communication manager
AN_service/         Analytics
spa/                React frontend
imports/            Keycloak realm export
jmx-exporter/       JMX Prometheus agent and Kafka rules
compose.yaml        Every container in the system
load-kafka-connectors.sh
*-config.yaml       Prometheus, Loki, Promtail and Grafana configuration
src/                Root Spring Boot module, kept as the Gradle root project
```

## Notes on the current state

- The SPA covers the contact pages only: a navigation bar, a principal view and a contact list and form. The other services have no frontend and are reached directly through the gateway.
- `AN_service` listens on a single `topicTest` topic and prints what it receives; no analytics are computed.
- `src/` at the root contains only an empty Spring Boot application class, kept so that the root project builds.
- `.env` in the repository is committed with real Keycloak client secrets and placeholders for the Gmail credentials.
