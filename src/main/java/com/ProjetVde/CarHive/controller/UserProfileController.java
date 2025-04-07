package com.ProjetVde.CarHive.controller;

import com.ProjetVde.CarHive.entity.User;
import com.ProjetVde.CarHive.entity.UserProfile;
import com.ProjetVde.CarHive.repository.UserRepository;
import com.ProjetVde.CarHive.service.UserProfileService;
import com.ProjetVde.CarHive.utils.ApiResponse;
import com.ProjetVde.CarHive.utils.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController // Use RestController for JSON responses
@RequestMapping("/api/userProfile")
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;

    @Autowired
    public UserProfileController(UserProfileService userProfileService, UserRepository userRepository, SecurityUtils securityUtils) {
        this.userProfileService = userProfileService;
        this.userRepository = userRepository;
        this.securityUtils = securityUtils;
    }

    @PostMapping
    public ResponseEntity<?> createUserProfile(@Valid @RequestBody UserProfile userProfile) {
        try {
            // Récupérer l'email de l'utilisateur connecté depuis Spring Security
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String userEmail;

            if (principal instanceof UserDetails) {
                userEmail = ((UserDetails) principal).getUsername();
            } else {
                userEmail = principal.toString();
            }

            // Récupérer l'utilisateur en base de données
            Optional<User> userOptional = userRepository.findByEmail(userEmail);
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"User not found with email: " + userEmail + "\"}");
            }
            User user = userOptional.get();
            userProfile.setUser(user);

            if (userProfile.getFirstName() == null || userProfile.getLastName() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"First name and last name are required\"}");
            }

            UserProfile createdProfile = userProfileService.create(userProfile);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProfile);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"An error occurred while registering the user profile: " + e.getMessage() + "\"}");
        }
    }

    @PutMapping("/{id}")
    public  ResponseEntity<?> UpdateUserProfile(@PathVariable Long id, @Valid @RequestBody UserProfile userProfile) {
        try {

            ResponseEntity<?> getUProfile = securityUtils.getAuthenticatedUserProfile();

            if (getUProfile.getStatusCode() != HttpStatus.OK) {
                return getUProfile;
            }
            UserProfile existingProfile = userProfileService.getById(id);
            if (existingProfile == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"UserProfile not found with id: " + id + "\"}");
            }

            existingProfile.setFirstName(userProfile.getFirstName());
            existingProfile.setLastName(userProfile.getLastName());

            userProfileService.update(existingProfile);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Profile saved successfully"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"An error occurred while registering the user profile: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping
    public ResponseEntity<?> getUserProfile() {
        try {
            ResponseEntity<?> response = securityUtils.getAuthenticatedUserProfile();
            if (response.getStatusCode() != HttpStatus.OK) {

                return response;
            }
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An error occurred while retrieving the user profile", "message", e.getMessage()));
        }
    }
}