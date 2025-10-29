package com.aslmk.authenticationservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@OpenAPIDefinition(
        info = @Info(
                title = "Auth Service Api",
                description = """
                        Educational authentication and authorization service built with Spring Boot,
                        featuring sessions, OAuth2, 2FA, and email verification.
                        """,
                version = "v1.0.0",
                license = @License(
                        name = "MIT License",
                        url = "https://opensource.org/licenses/MIT"
                ),
                contact = @Contact(
                        name = "Alemkhan Salimzhanov",
                        email = "aulaensov@gmail.com"
                )
        )
)
public class OpenApiConfig {}
