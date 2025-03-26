package com.ProjetVde.CarHive.service;

import com.ProjetVde.CarHive.entity.UserProfile;
import com.ProjetVde.CarHive.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserProfileService {

    @Autowired
    public UserProfileRepository userProfileRepository;

    public UserProfile create(UserProfile userProfile) {
        return userProfileRepository.save(userProfile);
    }
    public UserProfile update(UserProfile userProfile){
        return userProfileRepository.save(userProfile);
    }
    public void delete(Long id){
        if (userProfileRepository.existsById(id)) {
            userProfileRepository.deleteById(id);
        } else {
            // Ajouter un comportement si l'élément n'existe pas (ex : exception)
            throw new IllegalArgumentException("UserProfile not found with");
        }
    }
    public UserProfile getById(Long id){
        return userProfileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("UserProfile not found with"));
    }
}
