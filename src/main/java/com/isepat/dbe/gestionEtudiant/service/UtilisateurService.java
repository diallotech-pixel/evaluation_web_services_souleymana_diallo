/**
 * service pour gerer les utilisateurs, inscription et authentification
 * inscrire() : methode pour inscrire un utilisateur, verifier si l'email existe deja, encoder le mot de passe et sauvegarder l'utilisateur dans la base de donnees
 * authentifier() : methode pour authentifier un utilisateur, verifier si l'email existe, verifier le mot de
 */

package com.isepat.dbe.gestionEtudiant.service;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.isepat.dbe.gestionEtudiant.entity.Utilisateur;
import com.isepat.dbe.gestionEtudiant.repository.UtilisateurRepository;
import com.isepat.dbe.gestionEtudiant.config.JwtUtil;

@Service
public class UtilisateurService{
    //
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    //constructeur de UtilsiateurService
    public UtilisateurService(UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil){
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        
    }
    //methode pour inscrire un utilisateur 
    public Utilisateur inscrire(Utilisateur utilisateur){
        //verifier si l'email existe deja
        if(utilisateurRepository.findByEmail(utilisateur.getEmail()).isPresent()){
            throw new RuntimeException("L'email existe deja");
        }
        //encoder le mot de passe
        utilisateur.setMotDePasse(passwordEncoder.encode(utilisateur.getMotDePasse()));
        utilisateur.setRole("USER");
        return utilisateurRepository.save(utilisateur);
    }
    // methode pour authentifier un utilisateur 
    public String authentifier(String email, String motDePasse){
        //verifier si l'email existe
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email ou mot de passe incorrecte"));
        
        //verifier le mot de passe saisis est correct ou pas        
        if(!passwordEncoder.matches(motDePasse, utilisateur.getMotDePasse())){
            throw new RuntimeException("Email ou mot de passe incorrecte");
        }
        //generer le token JWT
        return jwtUtil.genererToken(utilisateur.getEmail(), utilisateur.getRole());
    }
}