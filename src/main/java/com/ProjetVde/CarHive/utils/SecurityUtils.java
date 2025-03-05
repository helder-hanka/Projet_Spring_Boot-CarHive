package com.ProjetVde.CarHive.utils;

import com.ProjetVde.CarHive.entity.User;
import com.ProjetVde.CarHive.entity.UserProfile;
import com.ProjetVde.CarHive.repository.UserRepository;
import com.ProjetVde.CarHive.service.UserProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SecurityUtils {

    private final UserRepository userRepository;
    private final UserProfileService userProfileService;

    public SecurityUtils(UserRepository userRepository, UserProfileService userProfileService) {
        this.userRepository = userRepository;
        this.userProfileService = userProfileService;
    }

    public ResponseEntity<?> getAuthenticatedUserProfile() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userEmail;

        if (principal instanceof UserDetails) {
            userEmail = ((UserDetails) principal).getUsername();
        } else {
            userEmail = principal.toString();
        }

        Optional<User> userOptional = userRepository.findByEmail(userEmail);
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found with email: " + userEmail);
        }

        User user = userOptional.get();
        UserProfile userProfile = userProfileService.getById(user.getId());

        if (userProfile == null) {
            return ResponseEntity.badRequest().body("L'utilisateur n'existe pas");
        }

        return ResponseEntity.ok(userProfile);
    }
}
