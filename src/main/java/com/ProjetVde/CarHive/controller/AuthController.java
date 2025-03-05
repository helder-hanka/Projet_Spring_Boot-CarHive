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
        if(user.getEmail() == null || user.getPassword() == null || user.getPassword().isEmpty()){
            return ResponseEntity.badRequest().body("Incorrect email or password");
        }
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getEmail(),
                            user.getPassword()
                    )
            );
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return ResponseEntity.ok(jwtUtils.generateToken(userDetails.getUsername()));
        }catch (BadCredentialsException e){
            return ResponseEntity.badRequest().body("Incorrect email or password");
        }
    }
    @PostMapping("/signup")
    public ResponseEntity <?> registerUser(@RequestBody User user) {
        if(user.getEmail() == null || user.getPassword() == null || user.getPassword().isEmpty() || user.getRole() == null){
            return ResponseEntity.badRequest().body("L'email or password or role est vide");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("Email address already in use.");
        }
        try {
            // Create new user's account
            User newUser = new User();
            newUser.setId(null);
            newUser.setEmail(user.getEmail());
            newUser.setPassword(encoder.encode(user.getPassword()));
            newUser.setRole(user.getRole());
            userRepository.save(newUser);
            return ResponseEntity.ok("User registered successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred while registering the user: " + e.getMessage());
        }

    }
}
