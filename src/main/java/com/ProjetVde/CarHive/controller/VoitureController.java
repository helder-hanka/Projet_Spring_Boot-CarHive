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
        Map<String, String> resBody = new HashMap<>();
        if (bindingResult.hasErrors()) {
            // Construire un message d'erreur
            bindingResult.getFieldErrors().forEach(error ->
                    resBody.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(resBody);
        }

        try {
            ResponseEntity<?> response = securityUtils.getAuthenticatedUserProfile();
            UserProfile userProfile = (UserProfile) response.getBody();

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
            voiture.setImageUrl(voitureRequest.getImageUrl());
            voiture.setUserProfile(userProfile);

            // Sauvegarde et retour de l'objet
            Voiture createdVoiture = voitureService.create(voiture);
            resBody.put("message", "Create successful");
            resBody.put("car", String.valueOf(createdVoiture));
            return ResponseEntity.ok(resBody);
        } catch (Exception e) {
            resBody.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(resBody);
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
        try {
            Optional<Voiture> voitureOp = voitureService.getById(voitureId);
            if(voitureOp.isEmpty()) {
                return ResponseEntity.badRequest().body("La voiture n'existe pas");
            }
            Voiture voiture = voitureOp.get();
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
System.out.println("voitureRequest: => "+voitureRequest.getAdresseGarage());
        try{
            ResponseEntity<?> response = securityUtils.getAuthenticatedUserProfile();
            UserProfile userProfile = (UserProfile) response.getBody();

            Optional<Voiture> voitureOp = voitureService.getById(id);
            if(voitureOp.isEmpty()){
                return ResponseEntity.badRequest().body("La voiture n'existe pas");
            }
            Voiture voiture = voitureOp.get();

            voiture.setImmatriculation(voitureRequest.getImmatriculation());
            voiture.setMarque(voitureRequest.getMarque());
            voiture.setModele(voitureRequest.getModele());
            voiture.setAnnee(voitureRequest.getAnnee());
            voiture.setImageUrl(voitureRequest.getImageUrl());
            assert userProfile != null;
            voiture.setUserProfile(userProfileService.getById(userProfile.getId()));

            // Gérer la couleur
            // Vérifier si la couleur existe, sinon la créer
            Optional<Color> colorOpt = colorService.getByName(voitureRequest.getColor());
            Color color = colorOpt.orElseGet(() -> {
                Color newColor = new Color();
                newColor.setColor(voitureRequest.getColor());
                return colorService.create(newColor);
            });
            voiture.setColor(color);
            // Gérer le garage
            Optional<Garage> garageOpt = garageService.getByName(voitureRequest.getNomGarage());
            Garage garage;

            if (garageOpt.isPresent()) {
                // Le garage existe, on vérifie si l'adresse ou le téléphone ont changé
                garage = garageOpt.get();
                if (!garage.getAdresse().equals(voitureRequest.getAdresseGarage()) ||
                        !garage.getTelephone().equals(voitureRequest.getTelephoneGarage())) {
                    garage.setAdresse(voitureRequest.getAdresseGarage());
                    garage.setTelephone(voitureRequest.getTelephoneGarage());
                    garageService.update(garage); // Mise à jour du garage existant
                }
            } else {
                // Le garage n'existe pas, on le crée
                Garage newGarage = new Garage();
                newGarage.setNom(voitureRequest.getNomGarage());
                newGarage.setAdresse(voitureRequest.getAdresseGarage());
                newGarage.setTelephone(voitureRequest.getTelephoneGarage());
                garage = garageService.create(newGarage);
            }
            voiture.setGarage(garage);
            Voiture updateVoiture = voitureService.update(voiture);
            return ResponseEntity.ok(updateVoiture);
        }catch (Exception e) {
            return ResponseEntity.internalServerError().body("Une erreur est survenue : " + e.getMessage());
        }

    }

    @DeleteMapping("/{voitureId}")
    public ResponseEntity<?> deleteById(@PathVariable Long voitureId ){

        try{
            ResponseEntity<?> response = securityUtils.getAuthenticatedUserProfile();
            UserProfile userProfile = (UserProfile) response.getBody();

            Optional<Voiture> voitureOpt = voitureService.getById(voitureId);
            if (voitureOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("La voiture n'existe pas");
            }
            Voiture voiture = voitureOpt.get();

            // Vérifier que l'utilisateur a bien la voiture (si besoin)
            assert userProfile != null;
            if (!voiture.getUserProfile().getId().equals(userProfile.getId())) {
                return ResponseEntity.badRequest().body("Cette voiture ne vous appartient pas");
            }
            voitureService.delete(voitureId);
            return ResponseEntity.ok(Map.of("message","Voiture supprimée avec succès"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Une erreur est survenue : " + e.getMessage());
        }
    }
}
