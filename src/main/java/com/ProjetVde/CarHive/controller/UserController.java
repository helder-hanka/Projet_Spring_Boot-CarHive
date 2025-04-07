package com.ProjetVde.CarHive.controller;

import com.ProjetVde.CarHive.entity.UserProfile;
import com.ProjetVde.CarHive.entity.Voiture;
import com.ProjetVde.CarHive.service.VoitureService;
import com.ProjetVde.CarHive.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    SecurityUtils securityUtils;
    @Autowired
    VoitureService voitureService;

    @GetMapping("/cars")
    public ResponseEntity<?>getMyCars(){
        try{
            ResponseEntity<?> response = securityUtils.getAuthenticatedUserProfile();
            UserProfile userProfile = (UserProfile) response.getBody();
            if (response.getStatusCode() != HttpStatus.OK) {
                return response;
            }
            if (userProfile == null || userProfile.getId() == null) {
                return ResponseEntity.badRequest().body("Could not retrieve user information.");
            }
            List<Voiture>myCars = voitureService.getByUserProfileId(userProfile.getId());
            return ResponseEntity.ok(myCars);


        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Une erreur est survenue : " + e.getMessage());

        }
    }
}
