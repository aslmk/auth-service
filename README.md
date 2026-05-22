# Auth-Service

Auth-Service is an **educational project** built to explore and understand how Spring Security, OAuth2, and related authentication mechanisms work internally.
It demonstrates the implementation of core security features such as **session-based authentication**, **OAuth2 integration (Google)**, **two-factor authentication**, **email verification**, **password recovery**, and **ReCaptcha**.

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
- ReCaptcha validation (registration, login, password recovery) 
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


## ⚙️ Configuration

Before starting the application, configure the `.env` file in the project root.
A template with all required variables is provided in `.env.example`.

### Environment Variables

| Variable                      | Description                  | Example                                            |
|-------------------------------|------------------------------|----------------------------------------------------|
| `DB_NAME`                     | PostgreSQL database name     | `auth-service-db`                                  |
| `DB_USER`                     | PostgreSQL username          | `postgres`                                         |
| `DB_PASSWORD`                 | PostgreSQL password          | `secret`                                           |
| `SPRING_DATASOURCE_URL`       | JDBC connection URL          | `jdbc:postgresql://postgres:5432/auth-service-db`  |
| `GOOGLE_RECAPTCHA_SITE_KEY`   | reCAPTCHA v2 Site Key        | from Google reCAPTCHA console                      |
| `GOOGLE_RECAPTCHA_SECRET_KEY` | reCAPTCHA v2 Secret Key      | from Google reCAPTCHA console                      |
| `GOOGLE_RECAPTCHA_VERIFY_URL` | Google verification endpoint | `https://www.google.com/recaptcha/api/siteverify`  |
| `MAIL_HOST`                   | SMTP host                    | `smtp.gmail.com`                                   |
| `MAIL_PORT`                   | SMTP port                    | `587`                                              |
| `MAIL_USERNAME`               | Sender email address         | `yourapp@gmail.com`                                |
| `MAIL_PASSWORD`               | App-specific SMTP password   | from Gmail App Passwords                           |
| `GOOGLE_OAUTH_CLIENT_ID`      | Google OAuth2 Client ID      | from Google Cloud Console                          |
| `GOOGLE_OAUTH_CLIENT_SECRET`  | Google OAuth2 Client Secret  | from Google Cloud Console                          |
| `GOOGLE_OAUTH_REDIRECT_URI`   | OAuth2 callback URL          | `http://localhost:8080/auth/oauth/callback/google` |

### How to Get the Keys

**reCAPTCHA:**
1. Go to [google.com/recaptcha/admin](https://www.google.com/recaptcha/admin)
2. Create a new site → choose **reCAPTCHA v2 ("I'm not a robot")**
3. Add `localhost` to the allowed domains
4. Copy the **Site Key** and **Secret Key** into your `.env`

**Google OAuth2:**
1. Go to [console.cloud.google.com](https://console.cloud.google.com)
2. Create a new project → go to **APIs & Services → Credentials**
3. Click **Create Credentials** → **OAuth 2.0 Client ID**
4. Add `http://localhost:8080/auth/oauth/callback/google` as an authorized redirect URI
5. Copy the **Client ID** and **Client Secret** into your `.env`

**Gmail SMTP:**
1. Enable 2-Step Verification on your Google account
2. Go to [myaccount.google.com/apppasswords](https://myaccount.google.com/apppasswords) — App Passwords are only accessible via this direct link, Google removed it from the UI
3. Generate a password → use it as `MAIL_PASSWORD`


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
   
2. Copy the environment template and fill in your values:
   ```bash
   cp .env.example .env
   ```

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


## 🔐 reCAPTCHA Integration Details

This project uses **reCAPTCHA v2** to protect the following endpoints: registration, login, and password recovery.

### How It Works

The reCAPTCHA token is validated server-side via a Spring AOP aspect (`@ValidateRecaptcha`).
The token must be passed in a **request header**:

```
Header name: recaptcha
Header value: <token from grecaptcha.getResponse()>
```

Example request to the registration endpoint:

```http
POST /auth/register
Content-Type: application/json
recaptcha: <your_recaptcha_token>
 
{
  "username": "john",
  "email": "john@example.com",
  "password": "secret123",
  "confirmPassword": "secret123"
}
```

If the token is missing or invalid, the server returns an error before any business logic runs.


## Contributing

Contributions and suggestions are welcome.
This is an educational project, so feedback and improvements are encouraged.

## License

This project is licensed under the MIT License.
