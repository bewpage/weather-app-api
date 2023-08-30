# Weather App API - Java/Spring

## Project Description

`weather-app-spring` is a robust and feature-rich weather application built with Java and Spring Boot. The application
allows you to fetch real-time weather information using the OpenWeather API and offers a RESTful API that can be easily
navigated with the included Swagger documentation.

## Features

- Fetch real-time weather information by city name, zip code, or coordinates.
- RESTful API for easy integration with other services.
- Built-in Swagger for API documentation and testing.
- Docker Compose setup for running the application with MySQL.

## Requirements

- Java 11+
- Spring Boot
- Maven
- OpenWeather API Key
- Docker (optional)
- Docker Compose (optional)

## Installation

### Environment Variables

Before proceeding with the standard or Docker Compose setup, make sure to set the following environment variables:

- for `SPRING_PROFILES_ACTIVE` it is possible to use `dev` or `prod` profile, depending on the environment you want to
   run the application,
- for `JWT_SECRET_KEY` it is possible to use the `KeyGenerator` utility class to generate a secret key for the JWT
   token. (see `src/main/java/com/bewpage/weatherapp/util/KeyGenerator.java`)

- `SPRING_PROFILES_ACTIVE=<profile_variable>`
- `JWT_SECRET_KEY=<create_token_with_util_KeyGenerator>`

### Standard Setup

1. **Clone the repository:**
   ```
   git clone https://github.com/bewpage/weather-app-api.git
   ```

2. **Navigate to the project directory:**
   ```
   cd weather-app-api
   ```

3. **Create an OpenWeather API key:**
   Sign up at [OpenWeather](https://openweathermap.org/appid) to get your API key.

4. **Add OpenWeather API Key:**
   Create a new file named `application-secret.properties` inside the `resources` folder and add your OpenWeather API
   Key.
   ```
   OPENWEATHER_API_KEY=<your-api-key-here>
   ```

### Docker Compose Setup (Optional)

1. **Navigate to the project directory.**

2. **Run services with Docker Compose:**
   ```
   docker-compose -p <here-your-project-name> -f ./docker/docker-compose.yml up -d
   ```
   This will start the Spring Boot application and MySQL database, making it even easier to manage both.

### Running the App

1. **Build the project:**
   ```
   mvn clean install
   ```

2. **Run the Spring Boot app:**
   ```
   mvn spring-boot:run
   ```

   Or if using Docker Compose:
   ```
   docker-compose up
   ```

The application should now be running on [http://localhost:8080](http://localhost:8080).

## API Documentation

- After starting the application, visit [Swagger UI](http://localhost:8080/swagger-ui.html) to see the available RESTful
  endpoints and to test them interactively.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.

---

Enjoy weather hunting! 🌤☔️💨