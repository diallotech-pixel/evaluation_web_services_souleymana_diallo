package com.isepat.dbe.gestionEtudiant.config;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import jakarta.annotation.PostConstruct;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private SecretKey cleSecrete;

    private final long dureeValiditeMs = 3600000;

    @PostConstruct
    public void init() {
        this.cleSecrete = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // methode pour generer le token (deja existante)
    public String genererToken(String email, String role) {
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + dureeValiditeMs))
                .signWith(cleSecrete)
                .compact();
    }

    // NOUVELLE methode : lit un token, verifie sa signature et son expiration,
    // puis extrait les informations (claims) qu'il contient
    public Claims extraireClaims(String token) {
        return Jwts.parser()
                .verifyWith(cleSecrete)  // verifie que la signature correspond bien a notre cle secrete
                .build()
                .parseSignedClaims(token) // decode le token ; leve une exception si signature invalide OU si expire
                .getPayload();            // recupere le contenu (email, role, dates...)
    }

    // methode pratique pour recuperer directement l'email (le "subject") depuis un token
    public String extraireEmail(String token) {
        return extraireClaims(token).getSubject();
    }

    // methode pratique pour recuperer directement le role depuis un token
    public String extraireRole(String token) {
        return extraireClaims(token).get("role", String.class);
    }
}