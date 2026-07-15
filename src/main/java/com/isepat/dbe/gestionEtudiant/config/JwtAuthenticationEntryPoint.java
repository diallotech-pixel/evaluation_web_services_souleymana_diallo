package com.isepat.dbe.gestionEtudiant.config;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    // Cette methode est appelee AUTOMATIQUEMENT par Spring Security
    // des qu'une requete non authentifiee touche une route protegee.
    // Elle remplace le comportement par defaut (qui produisait un 403)
    // par une reponse 401 explicite, au format JSON attendu par le sujet.
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                          AuthenticationException authException) throws IOException, ServletException {

        // Definit le code HTTP exact attendu par le sujet (401, pas 403)
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        // Construit le corps de reponse JSON, au meme format que le reste de l'API
        Map<String, Object> body = Map.of(
                "code", 401,
                "msg", "Authentification requise. Veuillez fournir un Token JWT valide."
        );

        // Convertit la Map en JSON et l'ecrit directement dans la reponse HTTP
        new ObjectMapper().writeValue(response.getOutputStream(), body);
    }
}