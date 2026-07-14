package com.isepat.dbe.gestionEtudiant.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.isepat.dbe.gestionEtudiant.entity.Etudiant;
import com.isepat.dbe.gestionEtudiant.service.EtudiantService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/etudiants")
@Tag(name = "Etudiants", description = "Gestion des etudiants de l'ISEP-AT")
public class EtudiantController {

    private final EtudiantService etudiantService;

    public EtudiantController(EtudiantService etudiantService) {
        this.etudiantService = etudiantService;
    }

    // POST /etudiants - ajouter un etudiant
    @Operation(
        summary = "Ajouter un etudiant",
        description = "Cree un nouvel etudiant si tous les champs obligatoires sont renseignes, "
                + "et si le matricule et l'email n'existent pas deja."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Etudiant cree avec succes"),
        @ApiResponse(responseCode = "400", description = "Un champ obligatoire est manquant"),
        @ApiResponse(responseCode = "409", description = "Le matricule ou l'email existe deja")
    })
    @PostMapping
    public ResponseEntity<?> ajouter(@RequestBody Etudiant etudiant) {

        if (etudiant.getMatricule() == null || etudiant.getMatricule().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("code", 400, "msg", "Le matricule est obligatoire."));
        }
        if (etudiant.getPrenom() == null || etudiant.getPrenom().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("code", 400, "msg", "Le prenom est obligatoire."));
        }
        if (etudiant.getNom() == null || etudiant.getNom().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("code", 400, "msg", "Le nom est obligatoire."));
        }
        if (etudiant.getEmail() == null || etudiant.getEmail().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("code", 400, "msg", "L'email est obligatoire."));
        }
        if (etudiant.getDateNaissance() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("code", 400, "msg", "La date de naissance est obligatoire."));
        }
        if (etudiant.getLieuNaissance() == null || etudiant.getLieuNaissance().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("code", 400, "msg", "Le lieu de naissance est obligatoire."));
        }
        if (etudiant.getNationalite() == null || etudiant.getNationalite().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("code", 400, "msg", "La nationalite est obligatoire."));
        }

        if (etudiantService.matriculeExist(etudiant.getMatricule())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("code", 409, "msg", "Le matricule existe deja."));
        }
        if (etudiantService.emailExist(etudiant.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("code", 409, "msg", "L'email existe deja."));
        }

        Etudiant etudiantCree = etudiantService.ajouterEtudiant(etudiant);
        return ResponseEntity.status(HttpStatus.CREATED).body(etudiantCree);
    }

    // methode lister tous les etudiants
    @Operation(
        summary = "Lister les etudiants",
        description = "Renvoie la liste complete de tous les etudiants enregistres."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des etudiants renvoyee avec succes")
    })
    @GetMapping
    public ResponseEntity<List<Etudiant>> lister() {
        return ResponseEntity.ok(etudiantService.listerEtudiants());
    }

    // methode pour rechercher un etudiant par son id
    @Operation(
        summary = "Rechercher un etudiant par id",
        description = "Renvoie l'etudiant correspondant a l'id fourni dans l'URL."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Etudiant trouve"),
        @ApiResponse(responseCode = "404", description = "Aucun etudiant ne correspond a cet id")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> rechercher(@PathVariable Long id) {
        try {
            Etudiant etudiant = etudiantService.rechercherEtudiant(id);
            return ResponseEntity.ok(etudiant);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("code", 404, "msg", e.getMessage()));
        }
    }

    // methode pour modifier un etudiant
    @Operation(
        summary = "Modifier un etudiant",
        description = "Met a jour les informations d'un etudiant existant, identifie par son id. "
                + "Verifie l'existence de l'etudiant, la validite des champs, puis l'unicite "
                + "du matricule et de l'email."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Etudiant modifie avec succes"),
        @ApiResponse(responseCode = "400", description = "Un champ obligatoire est manquant"),
        @ApiResponse(responseCode = "404", description = "Aucun etudiant ne correspond a cet id"),
        @ApiResponse(responseCode = "409", description = "Le nouveau matricule ou email existe deja")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> modifier(@PathVariable Long id, @RequestBody Etudiant etudiant) {

        Etudiant existant;
        try {
            existant = etudiantService.rechercherEtudiant(id);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("code", 404, "msg", e.getMessage()));
        }

        if (etudiant.getMatricule() == null || etudiant.getMatricule().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("code", 400, "msg", "Le matricule est obligatoire."));
        }
        if (etudiant.getPrenom() == null || etudiant.getPrenom().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("code", 400, "msg", "Le prenom est obligatoire."));
        }
        if (etudiant.getNom() == null || etudiant.getNom().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("code", 400, "msg", "Le nom est obligatoire."));
        }
        if (etudiant.getEmail() == null || etudiant.getEmail().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("code", 400, "msg", "L'email est obligatoire."));
        }
        if (etudiant.getDateNaissance() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("code", 400, "msg", "La date de naissance est obligatoire."));
        }
        if (etudiant.getLieuNaissance() == null || etudiant.getLieuNaissance().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("code", 400, "msg", "Le lieu de naissance est obligatoire."));
        }
        if (etudiant.getNationalite() == null || etudiant.getNationalite().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("code", 400, "msg", "La nationalite est obligatoire."));
        }

        if (!existant.getMatricule().equals(etudiant.getMatricule()) && etudiantService.matriculeExist(etudiant.getMatricule())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("code", 409, "msg", "Le matricule existe deja."));
        }
        if (!existant.getEmail().equals(etudiant.getEmail()) && etudiantService.emailExist(etudiant.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("code", 409, "msg", "L'email existe deja."));
        }

        existant.setMatricule(etudiant.getMatricule());
        existant.setPrenom(etudiant.getPrenom());
        existant.setNom(etudiant.getNom());
        existant.setEmail(etudiant.getEmail());
        existant.setDateNaissance(etudiant.getDateNaissance());
        existant.setLieuNaissance(etudiant.getLieuNaissance());
        existant.setNationalite(etudiant.getNationalite());

        Etudiant etudiantModifie = etudiantService.modifierEtudiant(existant);
        return ResponseEntity.ok(etudiantModifie);
    }

    // method pour supprimer un etudiant vai l'id
    @Operation(
        summary = "Supprimer un etudiant",
        description = "Supprime l'etudiant correspondant a l'id fourni dans l'URL."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Etudiant supprime avec succes, aucun contenu renvoye"),
        @ApiResponse(responseCode = "404", description = "Aucun etudiant ne correspond a cet id")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> supprimer(@PathVariable Long id) {
        try {
            etudiantService.supprimerEtudiant(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("code", 404, "msg", e.getMessage()));
        }
    }
}