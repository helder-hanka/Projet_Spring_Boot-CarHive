package com.ProjetVde.CarHive.controller;

import com.ProjetVde.CarHive.entity.*;
import com.ProjetVde.CarHive.repository.UserRepository;
import com.ProjetVde.CarHive.service.ColorService;
import com.ProjetVde.CarHive.service.GarageService;
import com.ProjetVde.CarHive.service.UserProfileService;
import com.ProjetVde.CarHive.service.VoitureService;
import com.ProjetVde.CarHive.utils.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

//@PreAuthorize("isAuthenticated()")
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
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SecurityUtils securityUtils;

    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @RequestBody VoitureRequest voitureRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            // Construire un message d'erreur
            Map<String, String> erreurs = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    erreurs.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(erreurs);
        }

        ResponseEntity<?> response = securityUtils.getAuthenticatedUserProfile(); // 🔹 Utilisation de SecurityUtils
        if (!response.getStatusCode().is2xxSuccessful()) {
            return response;
        }
        UserProfile userProfile = (UserProfile) response.getBody();

        try {
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

    @GetMapping("/public")
    public ResponseEntity<?> getAll() {
        try {
            Iterable<Voiture> voitures = voitureService.getAll();
                    if(voitures == null) {
                        return ResponseEntity.badRequest().body("La voiture n'existe pas");
                    }
            return ResponseEntity.ok(voitures);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Une erreur est survenue : " + e.getMessage());
        }
    }

    @GetMapping("/public/{voitureId}")
    public ResponseEntity<?> getById(@PathVariable Long voitureId) {
        System.out.println("Get Id: "+voitureId);
        try {
            Voiture voiture = voitureService.getById(voitureId);
            if (voiture == null) {
                return ResponseEntity.badRequest().body("La voiture n'existe pas");
            }
            return ResponseEntity.ok(voiture);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Une erreur est survenue : " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> putById(@PathVariable Long id, @Valid @RequestBody VoitureRequest voitureRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // Construire un message d'erreur
            Map<String, String> erreurs = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    erreurs.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(erreurs);
        }

        ResponseEntity<?> response = securityUtils.getAuthenticatedUserProfile();
        if (!response.getStatusCode().is2xxSuccessful()) {
            return response;
        }
        UserProfile userProfile = (UserProfile) response.getBody();
        if (userProfile == null) {
            return ResponseEntity.badRequest().body("L'utilisateur n'existe pas");
        }
        try{
            Voiture voiture = voitureService.getById(id);
            if (voiture == null) {
                return ResponseEntity.badRequest().body("La voiture n'existe pas");
            }

            voiture.setImmatriculation(voitureRequest.getImmatriculation());
            voiture.setMarque(voitureRequest.getMarque());
            voiture.setModele(voitureRequest.getModele());
            voiture.setAnnee(voitureRequest.getAnnee());
            voiture.setUserProfile(userProfileService.getById(userProfile.getId()));

            // Gérer la couleur
            Color color = colorService.getByName(voitureRequest.getColor())
                    .orElseGet(() -> {
                        Color newColor = new Color();
                        newColor.setColor(voitureRequest.getColor());
                        return colorService.create(newColor);
                    });
            voiture.setColor(color);

            // Gérer le garage
            Garage garage = garageService.getByName(voitureRequest.getNomGarage())
                    .orElseGet(() -> {
                        Garage newGarage = new Garage();
                        newGarage.setNom(voitureRequest.getNomGarage());
                        newGarage.setAdresse(voitureRequest.getAdresseGarage());
                        newGarage.setTelephone(voitureRequest.getTelephoneGarage());
                        return garageService.create(newGarage);
                    });
            voiture.setGarage(garage);

            voitureService.update(voiture);
            return ResponseEntity.ok(voiture);
        }catch (Exception e) {
            return ResponseEntity.internalServerError().body("Une erreur est survenue : " + e.getMessage());
        }

    }

    @DeleteMapping("/{voitureId}")
    public ResponseEntity<?> deleteById(@PathVariable Long voitureId ){

        ResponseEntity<?> response = securityUtils.getAuthenticatedUserProfile();
        if (!response.getStatusCode().is2xxSuccessful()) {
            return response;
        }
        UserProfile userProfile = (UserProfile) response.getBody();
        // Vérifier si l'utilisateur existe
        if (userProfile == null) {
            return ResponseEntity.badRequest().body("L'utilisateur n'existe pas");
        }
        try{
            Voiture voiture = voitureService.getById(voitureId);  // Récupération de la voiture par son ID
            if (voiture == null) {
                return ResponseEntity.badRequest().body("La voiture n'existe pas");
            }

            // Vérifier que l'utilisateur a bien la voiture (si besoin)
            if (!voiture.getUserProfile().getId().equals(userProfile.getId())) {
                return ResponseEntity.badRequest().body("Cette voiture ne vous appartient pas");
            }
            voitureService.delete(voitureId);
            return ResponseEntity.ok("Voiture supprimée avec succès");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Une erreur est survenue : " + e.getMessage());
        }
    }
}
