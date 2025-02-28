package com.ProjetVde.CarHive.service;

import com.ProjetVde.CarHive.entity.User;
import com.ProjetVde.CarHive.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    public UserRepository userRepository;

    public User create(User user) {
        return userRepository.save(user);
    }
    public User update(User user) {
        return userRepository.save(user);
    }

    public User getById(Long id){
        return userRepository.findById(id).orElse(null);
    }
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
