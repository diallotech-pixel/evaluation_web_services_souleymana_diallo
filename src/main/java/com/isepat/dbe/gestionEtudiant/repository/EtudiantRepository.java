package com.isepat.dbe.gestionEtudiant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.isepat.dbe.gestionEtudiant.entity.Etudiant;

public interface EtudiantRepository extends JpaRepository<Etudiant,Long>{

    boolean existsByMatricule(String matricule);
    boolean existsByEmail(String email);
    
}