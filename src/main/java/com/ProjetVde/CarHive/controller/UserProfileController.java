package com.ProjetVde.CarHive.controller;

import com.ProjetVde.CarHive.entity.User;
import com.ProjetVde.CarHive.entity.UserProfile;
import com.ProjetVde.CarHive.repository.UserRepository;
import com.ProjetVde.CarHive.service.UserProfileService;
import com.ProjetVde.CarHive.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/api/userProfile")
public class UserProfileController {

    @Autowired
    public UserProfileService userProfileService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;
    @Autowired
    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @RequestMapping
    public ResponseEntity<?> postUserProfile( @RequestBody @Valid UserProfile userProfile){

        // 🔹 Récupérer l'email de l'utilisateur connecté depuis Spring Security
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userEmail;

        if (principal instanceof UserDetails) {
            userEmail = ((UserDetails) principal).getUsername();
        } else {
            userEmail = principal.toString();
        }

        // 🔹 Récupérer l'utilisateur en base de données
        Optional<User> userOptional = userRepository.findByEmail(userEmail);
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found with email: " + userEmail);
        }
        User user = userOptional.get();
        userProfile.setUser(user);

            if( userProfile.getFirstName() == null || userProfile.getLastName() == null ){
                return ResponseEntity.badRequest().body("Le prénom et le nom sont requis");
            }
        try{
            userProfileService.create(userProfile);
            return ResponseEntity.ok("User registered successfully");
        }catch (Exception e){
            return ResponseEntity.internalServerError().body("An error occurred while registering the user: " + e.getMessage());
        }
    }

    public ResponseEntity<?> getUserProfile(Long id){
        return ResponseEntity.ok(userProfileService.getById(id));
    }

}
