package com.ProjetVde.CarHive.service;

import com.ProjetVde.CarHive.entity.Voiture;
import com.ProjetVde.CarHive.repository.VoitureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VoitureService {

    @Autowired
    private VoitureRepository voitureRepository;

    public Voiture create(Voiture voiture) {
        return voitureRepository.save(voiture);
    }

    public Voiture update(Voiture voiture) {
        return voitureRepository.save(voiture);
    }

    public void delete(Long id) {
        voitureRepository.deleteById(id);
    }

    public List<Voiture> getAll() {
        return voitureRepository.findAll();
    }

    public Optional<Voiture> getById(Long id) {
        return voitureRepository.findById(id);
    }

    public List<Voiture>getByUserProfileId(Long userProfileId){
        return voitureRepository.findByUserProfileId(userProfileId);
    }

}
