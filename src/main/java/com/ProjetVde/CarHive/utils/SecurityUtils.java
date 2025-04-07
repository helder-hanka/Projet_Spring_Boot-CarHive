package com.ProjetVde.CarHive.utils;

import com.ProjetVde.CarHive.entity.User;
import com.ProjetVde.CarHive.entity.UserProfile;
import com.ProjetVde.CarHive.repository.UserProfileRepository;
import com.ProjetVde.CarHive.repository.UserRepository;
import com.ProjetVde.CarHive.service.UserProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class SecurityUtils {

    private final UserRepository userRepository;
    private final UserProfileService userProfileService;
    private  final  UserProfileRepository  uProfileRepos;

    public SecurityUtils(UserRepository userRepository, UserProfileService userProfileService, UserProfileRepository uProfileRepos) {
        this.userRepository = userRepository;
        this.userProfileService = userProfileService;
        this.uProfileRepos = uProfileRepos;
    }

    public ResponseEntity<?> getAuthenticatedUserProfile() {

        String userEmail = getAuthenticatedUserEmail();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));
        Optional<UserProfile> userProfile = uProfileRepos.findByUserId(user.getId());
        if (userProfile.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "User profile does not exist"));
        }
        return ResponseEntity.ok(userProfile.get());
    }
//        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        String userEmail;
//
//        if (principal instanceof UserDetails) {
//            userEmail = ((UserDetails) principal).getUsername();
//        } else {
//            userEmail = principal.toString();
//        }
//
//
//
//        Optional<User> userOptional = userRepository.findByEmail(userEmail);
//        System.out.println("User Email"+userOptional);
//        if (userOptional.isEmpty()) {
//            return ResponseEntity.badRequest().body("User not found with email: " + userEmail);
//        }
//
//        User user = userOptional.get();
//        UserProfile userProfile = userProfileService.getById(user.getId());
//
//        if (userProfile == null) {
//            return ResponseEntity.badRequest().body("L'utilisateur n'existe pas");
//        }
//
//        return ResponseEntity.ok(userProfile);
//    }

        public String getAuthenticatedUserEmail() {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return (principal instanceof UserDetails) ? ((UserDetails) principal).getUsername() : principal.toString();
        }
}
