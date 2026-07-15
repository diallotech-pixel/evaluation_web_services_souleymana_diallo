/**
 * cette classe ne fait rien d'autre que présenter cet objet à Spring.
 * @Configuration une classe dont le seul rôle est de fournir des objets à Spring
 * @Bean renvoie une instance de BCryptPasswordEncoder
 */
package com.isepat.dbe.gestionEtudiant.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityBeansConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}