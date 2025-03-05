package com.ProjetVde.CarHive.service;

import com.ProjetVde.CarHive.entity.User;
import com.ProjetVde.CarHive.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
}
