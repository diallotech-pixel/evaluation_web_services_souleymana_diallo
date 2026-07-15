package com.isepat.dbe.gestionEtudiant.config;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Lire l'en-tete Authorization envoye par le client
        String authHeader = request.getHeader("Authorization");

        //  Verifier qu'il commence bien par "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // enleve les 7 caracteres de "Bearer "

            try {
                // Valider le token et extraire l'email + le role
                String email = jwtUtil.extraireEmail(token);
                String role = jwtUtil.extraireRole(token);

                // Construire un objet Authentication reconnu par Spring Security,
                // avec le role converti au format attendu (prefixe "ROLE_")
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        email,
                        null, // pas besoin du mot de passe ici, le token suffit a prouver l'identite
                        List.of(new SimpleGrantedAuthority("ROLE_" + role))
                );

                //  Enregistrer cette authentification dans le contexte de securite,
                // valable uniquement pour cette requete precise
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                // Token invalide ou expire : on ne fait rien de special ici,
                // SecurityConfig se chargera de rejeter la requete avec un 401
                // si la route necessitait une authentification
                SecurityContextHolder.clearContext();
            }
        }

        //  Laisser la requete continuer son chemin (vers SecurityConfig puis le controleur)
        filterChain.doFilter(request, response);
    }
}