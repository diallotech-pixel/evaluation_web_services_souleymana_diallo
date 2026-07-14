package com.isepat.dbe.gestionEtudiant.service;
import java.util.List;

import org.springframework.stereotype.Service;

import com.isepat.dbe.gestionEtudiant.entity.Etudiant;
import com.isepat.dbe.gestionEtudiant.repository.EtudiantRepository;

@Service
public class EtudiantService{
    private final EtudiantRepository etudiantRepository;

    public EtudiantService(EtudiantRepository etudiantRepository) {
        this.etudiantRepository = etudiantRepository;
    }

    //methode pour ajouter un etudiant
    public Etudiant ajouterEtudiant(Etudiant etudiant) {
        return etudiantRepository.save(etudiant);
    }

    //methode pour modifier un etudiant
    public Etudiant modifierEtudiant(Etudiant etudiant){
        return etudiantRepository.save(etudiant);
    }

    //methode pour supprimer
    public void supprimerEtudiant(Long id){
        Etudiant etudiant = rechercherEtudiant(id);
        etudiantRepository.delete(etudiant);
    }

    //methode pour rechercher un etudiant
    public Etudiant rechercherEtudiant(Long id){
        return etudiantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Etudiant introuvable avec l'id : " + id));
    }

    //methode pour lister les etudiant
    public List<Etudiant> listerEtudiants(){
        return etudiantRepository.findAll();
    } 

    //verifier si l'email existe existe
    public Boolean emailExist(String email){
        return etudiantRepository.existsByEmail(email);
    }

    //verifier si la matricule existe
    public Boolean matriculeExist(String matricule){
        return etudiantRepository.existsByMatricule(matricule);
    }

}