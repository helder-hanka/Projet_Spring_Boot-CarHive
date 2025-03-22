package com.ProjetVde.CarHive.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.ProjetVde.CarHive.entity.User;
import com.ProjetVde.CarHive.repository.UserRepository;
import com.ProjetVde.CarHive.security.JwtUtil;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    JwtUtil jwtUtils;
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody User user) {
        Map<String, Object> response = new HashMap<>();

        if (user.getEmail() == null || user.getPassword() == null || user.getPassword().isEmpty()) {
            response.put("error", "Incorrect email or password");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtils.generateToken(userDetails.getUsername());

            response.put("message", "Login successful");
            response.put("token", token);

            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            response.put("error", "Incorrect email or password");
            return ResponseEntity.badRequest().body(response);
        }
    }
    @PostMapping("/signup")
    public ResponseEntity <?> registerUser(@RequestBody User user) {
        Map<String, String> response = new HashMap<>();

        if (user.getEmail() == null || user.getPassword() == null || user.getPassword().isEmpty() || user.getRole() == null) {
            response.put("error", "L'email, le mot de passe ou le rôle est vide.");
            return ResponseEntity.badRequest().body(response);
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            response.put("error", "Email address already in use.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // Création du nouvel utilisateur
            User newUser = new User();
            newUser.setId(null);
            newUser.setEmail(user.getEmail());
            newUser.setPassword(encoder.encode(user.getPassword()));
            newUser.setRole(user.getRole());
            userRepository.save(newUser);

            response.put("message", "User registered successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "An error occurred while registering the user: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
