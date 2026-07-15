package com.isepat.dbe.gestionEtudiant.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        // Nom donne au schema de securite, reutilise ci-dessous
        final String schemeName = "bearerAuth";

        return new OpenAPI()
                // Declare qu'un schema de securite "bearerAuth" existe et comment il fonctionne
                .components(new Components()
                        .addSecuritySchemes(schemeName, new SecurityScheme()
                                .name(schemeName)
                                .type(SecurityScheme.Type.HTTP)   // type HTTP standard
                                .scheme("bearer")                  // schema "Bearer <token>"
                                .bearerFormat("JWT")               // precise que c'est un JWT
                        )
                )
                // Applique ce schema par defaut a TOUTES les routes documentees
                .addSecurityItem(new SecurityRequirement().addList(schemeName));
    }
}