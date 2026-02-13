# Auth-Service

Auth-Service is an **educational project** built to explore and understand how modern authentication and authorization systems works internally.
It demonstrates the implementation of core security features such as **session-based authentication**, **OAuth2 integration (Google)**, **two-factor authentication**, **email verification**, **password recovery**, and **ReCaptcha**. All implemented manually using Spring Boot.

## ⚠️ Disclaimer

This project was created **for learning purposes only**.  
It is **not intended for production use** and may contain **security vulnerabilities or simplified implementations**.  
Use it as a reference for educational exploration or further development at your own risk.

## 🌟 Features

- Session-based authentication (without Spring Security)
- Manual OAuth2 login via Google
- Two-factor authentication (email codes)
- Email confirmation flow
- Password recovery mechanism
- Role-based access control
- ReCaptcha validation during registration
- User profile management
- Integration and unit testing

## 🔧 Tech Stack

- **Java 17**  
- **Spring Boot 3.4.4**
  - Spring Security Crypto
  - Spring Data JPA  
  - Spring Web  
  - Spring Validation  
  - Spring Mail
- **PostgreSQL**, **Flyway**
- **Maven**
- **JUnit 5**, **Mockito**, **Testcontainers**, **H2-database**
- **Docker** & **Docker Compose**, **Lombok**

## 🚀 Quick Start

### Prerequisites
- JDK 17+
- Maven 3.9+  
- Docker & Docker Compose

### Get Started Locally in 5 Minutes

1. Clone the repository:
   ```bash
   git clone https://github.com/aslmk/auth-service.git
   cd auth-service
   ```
   
2. Configure environment variables (e.g. database, mail service) in `.env` and `application.properties`.

3. Start the application using Docker:

   ```bash
   docker-compose up -d
   ```

4. Once all containers are running, the API will be available at:

   ```
   http://localhost:8080
   ```

## API Documentation

Available at http://localhost:8080/swagger-ui/index.html
(OpenAPI generated automatically via `springdoc-openapi`)

## Contributing

Contributions and suggestions are welcome.
This is an educational project, so feedback and improvements are encouraged.

## License

This project is licensed under the MIT License.
