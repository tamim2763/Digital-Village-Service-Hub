# Digital Village Service Hub

Digital Village Service Hub is a Spring Boot 3.x starter for a multilingual rural services platform. This repository only contains the foundation: package structure, starter configuration, sample user slice, and a Thymeleaf + Bootstrap UI shell. Business features will be added later by feature teams.

## Tech Stack

- Java 21
- Spring Boot 3.5.9
- Maven Wrapper
- Spring MVC
- Spring Data JPA
- Spring Security
- Thymeleaf
- Bootstrap 5
- MySQL
- Lombok
- Validation
- DevTools
- Actuator

## Why these dependencies are included

- `spring-boot-starter-web`: servlet-based web application support with Spring MVC.
- `spring-boot-starter-data-jpa`: repository and entity support for MySQL-backed persistence.
- `spring-boot-starter-security`: security foundation for future authentication and authorization.
- `spring-boot-starter-thymeleaf`: server-side rendering for the starter UI pages.
- `spring-boot-starter-validation`: Jakarta Bean Validation for DTOs and future forms.
- `spring-boot-starter-actuator`: production health and operational endpoints.
- `spring-boot-devtools`: faster local development restart cycle.
- `mysql-connector-j`: MySQL JDBC driver.
- `lombok`: reduces boilerplate in entities and service classes.
- `spring-boot-configuration-processor`: metadata support for future custom configuration properties.
- `spring-boot-starter-test`: standard testing support for the starter build.
- `spring-security-test`: security-aware testing support for future secured endpoints.

## Project Structure

- `com.digitalvillage.config`: shared configuration entry points.
- `com.digitalvillage.controller`: MVC pages and sample REST controller.
- `com.digitalvillage.service` and `service.impl`: service contract and implementation.
- `com.digitalvillage.repository`: Spring Data repositories.
- `com.digitalvillage.entity`: JPA entities.
- `com.digitalvillage.dto`: request/response transfer objects.
- `com.digitalvillage.mapper`: simple mapping layer for the sample slice.
- `com.digitalvillage.security`: Spring Security foundation.
- `com.digitalvillage.exception`: application exception types.
- `com.digitalvillage.util`: shared constants.
- `com.digitalvillage.model`: UI view models.

## Configuration Profiles

- `application.yml`: common settings.
- `application-dev.yml`: local MySQL defaults using environment-variable placeholders.
- `application-prod.yml`: production-oriented MySQL placeholders.

### MySQL placeholders

Both runtime profiles read the same MySQL variables:

- `MYSQL_JDBC_URL`
- `MYSQL_HOST`
- `MYSQL_PORT`
- `MYSQL_DATABASE`
- `MYSQL_USER`
- `MYSQL_PASSWORD`

If the variables are not set, the app falls back to:

- URL: `jdbc:mysql://localhost:3306/digital_village_service_hub?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC`
- User: `root`
- Password: `CE23017@lisan`

## Local Run

1. Set Java 21.
2. Install and start MySQL locally.
3. Create a database named `digital_village_service_hub`.
4. Make sure the `root` account can log in with password `CE23017@lisan`.
5. Run the app with the `dev` profile.

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

If you prefer the production settings, run with the `prod` profile instead.

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

## MySQL Setup

Use any MySQL client you like, such as MySQL Workbench or the MySQL command line.

Create the database and user before starting the app:

```sql
CREATE DATABASE digital_village_service_hub CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- If needed, update your root password in MySQL Workbench or the MySQL shell.
-- The app will connect with:
-- user: root
-- password: CE23017@lisan
```

If you want different credentials, set these environment variables before running the app:

```powershell
$env:MYSQL_HOST = 'localhost'
$env:MYSQL_PORT = '3306'
$env:MYSQL_DATABASE = 'digital_village_service_hub'
$env:MYSQL_USER = 'root'
$env:MYSQL_PASSWORD = 'CE23017@lisan'
```

Then start the app with:

```powershell
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
```

The `dev` profile uses port `8081`, so you can leave port `8080` for other tools if needed.

## Recommended Developer Setup

1. Install Java 21.
2. Install MySQL 8.x.
3. Create the database shown above.
4. Run the Spring Boot app with the `dev` profile.

MySQL is the intended development and production datastore for this starter.

## Development Notes

- Use constructor injection for new services and controllers.
- Keep business logic in `service` and `service.impl`.
- Keep MVC pages in `controller` and `templates`.
- Keep persistence types in `entity`, `repository`, and `mapper`.
- Add new feature modules without changing the sample user slice unless a shared contract changes.
- Add new static assets under `src/main/resources/static`.
- Add uploaded file handling later under `src/main/resources/uploads` or a dedicated storage service.
- Authentication is intentionally permissive for now; replace the security starter config when real login is added.

## Build

```bash
./mvnw clean test
```

## Starter Notes

- Authentication is intentionally not implemented yet.
- Business workflows, API endpoints, and persistence operations beyond the sample user slice will be added later.
- Uploaded files should go under `src/main/resources/uploads/` once that module exists.
