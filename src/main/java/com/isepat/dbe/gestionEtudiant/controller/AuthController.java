/**
 * controlleur pour gerer l'inscription et l'authentification des utilisateurs
 * register() : methode pour inscrire un utilisateur, verifier si l'email existe deja, encoder le mot de passe et sauvegarder l'utilisateur dans la base de donnees
 * @ResponseEntity : retourne un objet ResponseEntity avec le code de statut HTTP et le corps de la reponse
 * @RequestBody : indique que le corps de la requete HTTP doit etre converti en objet Utilisateur
 *
 * login() : methode pour authentifier un utilisateur, verifier si l'email existe, verifier le mot de passe saisis est correct ou pas et generer le token JWT
 */
package com.isepat.dbe.gestionEtudiant.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.isepat.dbe.gestionEtudiant.entity.Utilisateur;
import com.isepat.dbe.gestionEtudiant.service.UtilisateurService;

import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/auth")
public class AuthController {

    //attrbut de AuthController pour 
    private final UtilisateurService utilisateurService;

    //contructeur
    public AuthController(UtilisateurService utilisateurService){
        this.utilisateurService = utilisateurService;
    }

    //methode pour inscrire un utilisateur 
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Utilisateur utilisateur){
        try {
            Utilisateur utilisateurCreer = utilisateurService.inscrire(utilisateur);

            utilisateurCreer.setMotDePasse(null); //le mot de passe haché ne sera plus exposé au client 
            return ResponseEntity.status(HttpStatus.CREATED).body(utilisateurCreer);
        } catch (RuntimeException e) {
            // TODO: handle exception
            return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(Map.of("code", 409, "msg", e.getMessage()));
        }
    }

    //methode pour authentifier un utilisateur
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Utilisateur utilisateur){
        try {
            String token = utilisateurService.authentifier(utilisateur.getEmail(), utilisateur.getMotDePasse());
            return ResponseEntity.ok(Map.of("token", token));
        } catch (RuntimeException e) {
            // TODO: handle exception
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(Map.of("code", 401, "msg", e.getMessage()));
        }
    }
    
}
