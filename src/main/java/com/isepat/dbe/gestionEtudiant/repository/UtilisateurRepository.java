/**
 * ce couche nous permet directelement de communiqué avec la base de donnée sans ecrire du sql brut
 * du coup on a les methode : 
 * 1 . findByEmail pour recuperer l'email qu'on a passé en parametre 
 * 
 */
package com.isepat.dbe.gestionEtudiant.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.isepat.dbe.gestionEtudiant.entity.Utilisateur;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    
    Optional<Utilisateur> findByEmail(String email);
}
