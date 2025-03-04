package com.ProjetVde.CarHive.controller;

import com.ProjetVde.CarHive.entity.*;
import com.ProjetVde.CarHive.service.ColorService;
import com.ProjetVde.CarHive.service.GarageService;
import com.ProjetVde.CarHive.service.UserProfileService;
import com.ProjetVde.CarHive.service.VoitureService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/voiture")
public class VoitureController {

    @Autowired
    private VoitureService voitureService;

    @Autowired
    private GarageService garageService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private ColorService colorService;

    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @RequestBody VoitureRequest voitureRequest) {
        try {
            // Vérifier si l'utilisateur existe
            if (userProfileService.getById(Long.valueOf(voitureRequest.getUserId())) == null) {
                return ResponseEntity.badRequest().body("L'utilisateur n'existe pas");
            }
            UserProfile userProfile = userProfileService.getById(Long.valueOf(voitureRequest.getUserId()));
            if (userProfile == null) {
                return ResponseEntity.badRequest().body("L'utilisateur n'existe pas");
            }

            // Vérifier si la couleur existe, sinon la créer
            Optional<Color> colorOpt = colorService.getByName(voitureRequest.getColor());
            Color color = colorOpt.orElseGet(() -> {
                Color newColor = new Color();
                newColor.setColor(voitureRequest.getColor());
                return colorService.create(newColor);
            });

            // Vérifier si le garage existe, sinon le créer
            Optional<Garage> garageOpt = garageService.getByName(voitureRequest.getNomGarage());
            Garage garage = garageOpt.orElseGet(() -> {
                Garage newGarage = new Garage();
                newGarage.setNom(voitureRequest.getNomGarage());
                newGarage.setAdresse(voitureRequest.getAdresseGarage());
                newGarage.setTelephone(voitureRequest.getTelephoneGarage());
                return garageService.create(newGarage);
            });

            // Créer la voiture
            Voiture voiture = new Voiture();
            voiture.setImmatriculation(voitureRequest.getImmatriculation());
            voiture.setMarque(voitureRequest.getMarque());
            voiture.setModele(voitureRequest.getModele());
            voiture.setAnnee(voitureRequest.getAnnee());
            voiture.setGarage(garage);
            voiture.setColor(color);
            voiture.setUserProfile(userProfile);

            // Sauvegarde et retour de l'objet
            Voiture createdVoiture = voitureService.create(voiture);
            return ResponseEntity.ok(createdVoiture);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Une erreur est survenue : " + e.getMessage());
        }
    }
    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            return ResponseEntity.ok(voitureService.getAll());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Une erreur est survenue : " + e.getMessage());
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            Voiture voiture = voitureService.getById(id);
            if (voiture == null) {
                return ResponseEntity.badRequest().body("La voiture n'existe pas");
            }
            return ResponseEntity.ok(voiture);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Une erreur est survenue : " + e.getMessage());
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> putById(@PathVariable Long id, @Valid @RequestBody VoitureRequest voitureRequest) {
        try{
            // Vérifier si l'utilisateur existe
            if (userProfileService.getById(Long.valueOf(voitureRequest.getUserId())) == null) {
                return ResponseEntity.badRequest().body("L'utilisateur n'existe pas");
            }
            Voiture voiture = voitureService.getById(id);
            if (voiture == null){
                return ResponseEntity.badRequest().body("La voiture n'existe pas");
            }
            UserProfile userProfile = userProfileService.getById(Long.valueOf(voitureRequest.getUserId()));
            if (userProfile == null || !Objects.equals(userProfile.getId(), voiture.getUserProfile().getId())){
                return ResponseEntity.badRequest().body("L'utilisateur n'existe pas");
            }
            voiture.setImmatriculation(voitureRequest.getImmatriculation());
            voiture.setMarque(voitureRequest.getMarque());
            voiture.setModele(voitureRequest.getModele());
            voiture.setAnnee(voitureRequest.getAnnee());
            voiture.setUserProfile(userProfileService.getById(Long.valueOf(voitureRequest.getUserId())));
            voiture.setColor(colorService.getByName(voitureRequest.getColor()).orElse(null));
            voiture.setGarage(garageService.getByName(voitureRequest.getNomGarage()).orElse(null));
            voitureService.update(voiture);
            return ResponseEntity.ok(voiture);
        }catch (Exception e) {
            return ResponseEntity.internalServerError().body("Une erreur est survenue : " + e.getMessage());
        }

    }

    @DeleteMapping("/{voitureId}/{userId}")
    public ResponseEntity<?> deleteById(@PathVariable Long voitureId , @PathVariable Long userId){
        try{
            // Vérifier si l'utilisateur existe
            if (userProfileService.getById(userId) == null) {
                return ResponseEntity.badRequest().body("L'utilisateur n'existe pas");
            }
            Voiture voiture = voitureService.getById(voitureId);
            if (voiture == null){
                return ResponseEntity.badRequest().body("La voiture n'existe pas");
            }
            UserProfile userProfile = userProfileService.getById(userId);
            if (userProfile == null || !Objects.equals(userProfile.getId(), voiture.getUserProfile().getId())){
                return ResponseEntity.badRequest().body("L'utilisateur n'existe pas");
            }
            voitureService.delete(voitureId);
            return ResponseEntity.ok("Voiture supprimée avec succès");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Une erreur est survenue : " + e.getMessage());
        }
    }
}
