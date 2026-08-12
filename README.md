Hey Devs!! 

Welcome to CloudOps Platform!!

So Let's me introduce you first with this project - 

## What is CloudOps Platform ?

CloudOps is an AI-assisted container deployment and monitoring platform. Users submit Docker-based deployment requests, and the system validates, stores and tracks them through a controlled deployment lifecycle. The Java 21 and Spring Boot backend uses PostgreSQL for persistent data, Kafka for asynchronous operational events and Redis for idempotency or measured caching. Deployments are initially executed through a simulated provider, with an adapter boundary that can later support Kubernetes. Failed deployment logs are sent to a focused FastAPI analysis service that returns structured, evidence-based troubleshooting recommendations. I started with a modular monolith to keep transactions, testing and local development manageable while preserving clear module boundaries.

Now will guide you how to setup this --

## Local Setup

### Prerequisites

Install the following tools before running the project:

- Java Development Kit (JDK) 21
- Docker Desktop with Docker Compose
- Git

Maven does not need to be installed separately. The repository includes the Maven Wrapper, which downloads and uses the required Maven version.

Verify the required tools from PowerShell:

```powershell
java -version
javac -version
docker --version
docker compose version
git --version
```

Both `java` and `javac` should report version 21. Make sure Docker Desktop is running before continuing.

### 1. Open the repository

```powershell
cd C:\Project\cloudops-platform
```

### 2. Start PostgreSQL

Start the development database defined in `compose.yaml`:

```powershell
docker compose up -d postgres
```

Check its status:

```powershell
docker compose ps
```

Wait until the PostgreSQL container reports `healthy`.

CloudOps maps PostgreSQL to host port `5433` because port `5432` may already be used by another local database:

```text
Host: localhost
Port: 5433
Database: cloudops
Username: cloudops
Password: cloudops
```

These credentials are intended only for local development.

### 3. Run the tests

Move into the Spring Boot application directory:

```powershell
cd cloudops-api
```

Run the test suite:

```powershell
.\mvnw.cmd test
```

The integration test uses Testcontainers to start an isolated PostgreSQL container. Docker Desktop must therefore be running even when the Compose database is not required by the test itself.

### 4. Run the application

From the `cloudops-api` directory, run:

```powershell
.\mvnw.cmd spring-boot:run
```

The application starts on:

```text
http://localhost:8080
```

Verify its health endpoint:

```text
http://localhost:8080/actuator/health
```

Expected response:

```json
{
  "status": "UP"
}
```

Stop the application by pressing `Ctrl+C` in its terminal.

### 5. Configure a different database

The application uses the following local defaults:

```text
DB_URL=jdbc:postgresql://localhost:5433/cloudops
DB_USERNAME=cloudops
DB_PASSWORD=cloudops
```

Override them in the current PowerShell session when connecting to another database:

```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5433/cloudops"
$env:DB_USERNAME = "cloudops"
$env:DB_PASSWORD = "cloudops"
.\mvnw.cmd spring-boot:run
```

Do not commit production credentials or personal secrets to Git.

### 6. Stop the local database

From the repository root, run:

```powershell
docker compose down
```

This stops the container while preserving its named volume and database data. Using `docker compose down -v` also deletes the database volume, so use it only when you intentionally want to reset all local data.

### Common startup problem

If startup reports `password authentication failed for user "cloudops"`, confirm that:

- The CloudOps PostgreSQL container is healthy.
- `application.yml` connects to port `5433`.
- No environment variable overrides `DB_URL`, `DB_USERNAME` or `DB_PASSWORD` with an incorrect value.
- You started the database from the CloudOps repository rather than connecting to another PostgreSQL instance on port `5432`.


