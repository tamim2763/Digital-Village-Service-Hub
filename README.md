# Digital Village Service Hub

Digital Village Service Hub is an **HCI-compliant, Bengali-first** Spring Boot web application designed to provide essential public services to rural communities in Bangladesh. This project was built with a strong focus on Human-Computer Interaction (HCI) principles, ensuring that the platform is accessible, readable, and easy to navigate for its target demographic.

## Key Features

- **HCI-Optimized Clean UI**: A minimalistic, distraction-free "Light Theme" (inspired by OpenRouter) to reduce cognitive load.
- **Multilingual Support (i18n)**: Seamlessly toggle between Bengali (Default) and English.
- **Context-Aware Typography**: 
  - **Kalpurush**: Locally hosted, high-legibility Bengali font set as the default for all regional text.
  - **Inter**: Clean, modern sans-serif font specifically configured for English/Latin characters.
  - Typography scales automatically for perfect readability in both languages.
- **Train Ticket Booking UI**:
  - **Search Functionality**: Search for trains by departure and arrival stations, and date.
  - **Seat Selection**: Interactive 2x2 seat selection map with dynamic coach switching and real-time total fare calculations.
  - **Mock Backend Integration**: Filter active trains based on local time and dynamically generate class availabilities.
- **Weather & Agricultural Dashboard**:
  - **Hyper-Local Forecasts**: Real-time geolocation and dropdown-based location selection using the Open-Meteo API.
  - **Comprehensive Weather Data**: Current conditions, hourly rain probability, and a 7-day forecast.
  - **Intelligent Farmer Advisories**: Dynamic agricultural recommendations (Irrigation, Pesticide Spraying, Harvesting) calculated on-the-fly based on precipitation probability, rain volume, and wind speed.
- **Responsive Layout**: Built with Bootstrap 5 to ensure full compatibility across mobile devices and desktop screens.

## Tech Stack

- **Backend**: Java 21, Spring Boot 3.5.9, Spring MVC, Spring Data JPA, Spring Security
- **Frontend**: Thymeleaf, Bootstrap 5, Vanilla CSS
- **Database**: MySQL 8.x
- **Build Tool**: Maven Wrapper

## Project Structure

- `com.digitalvillage.config`: Shared configuration (e.g., i18n LocaleResolvers).
- `com.digitalvillage.controller`: MVC page routing and model injection.
- `com.digitalvillage.model`: UI view models (e.g., `ServiceCard`).
- `src/main/resources/messages.properties`: Primary Bengali translations.
- `src/main/resources/messages_en.properties`: English translations.
- `src/main/resources/static/css/main.css`: Core design system and typography rules.

## Local Development Setup

### 1. MySQL Setup

The application requires a MySQL database. Create the database and user before starting the app:

```sql
CREATE DATABASE digital_village_service_hub CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

The application is configured to look for the following credentials by default:
- **User**: `root`
- **Password**: `YOUR_PASSWORD`

If you are using different credentials, you must set them as environment variables before running the application:

```powershell
# Windows PowerShell Example
$env:MYSQL_HOST = 'localhost'
$env:MYSQL_PORT = '3306'
$env:MYSQL_DATABASE = 'digital_village_service_hub'
$env:MYSQL_USER = 'root'
$env:MYSQL_PASSWORD = 'YOUR_PASSWORD'
```

### 2. Running the Application

Ensure you have Java 21 installed. Use the Maven wrapper to run the application with the `dev` profile. The `dev` profile disables Thymeleaf caching for easier UI development and runs the server on port `8081`.

```bash
# Windows
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"

# Mac/Linux
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Once running, access the application at: **http://localhost:8081**

## Security & Version Control Notes

- **Environment Variables**: Never hardcode real database passwords or secrets in `application-dev.yml` or `application-prod.yml`. Always use environment variables (`${MYSQL_PASSWORD:YOUR_PASSWORD}`).
- **Local Overrides**: If you prefer using a file for your local secrets, create an `application-local.yml` file and run the app with the `local` profile. (Note: `*-local.yml` files are ignored by git).

## Build for Production

```bash
# Run tests and package into an executable JAR
.\mvnw.cmd clean package
```
