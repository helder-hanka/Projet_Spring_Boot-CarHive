package com.ProjetVde.CarHive.service;

import com.ProjetVde.CarHive.entity.Garage;
import com.ProjetVde.CarHive.repository.GarageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GarageService {
    @Autowired
    private GarageRepository garageRepository;

    public Garage create(Garage garage) {
        return garageRepository.save(garage);
    }

    public Garage update(Garage garage) {
        return garageRepository.save(garage);
    }

    public void delete(Long id) {
        garageRepository.deleteById(id);
    }
    public Garage getById(Long id){
        return garageRepository.findById(id).orElse(null);
    }
    public Optional<Garage> getByName(String nom){
        return Optional.ofNullable(garageRepository.findByNom(nom).or(() -> garageRepository.findByNom(nom.toLowerCase())).or(() -> garageRepository.findByNom(nom.toUpperCase())).orElse(null));
    }
}
