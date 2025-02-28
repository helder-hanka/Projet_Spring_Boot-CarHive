package com.ProjetVde.CarHive.service;

import com.ProjetVde.CarHive.entity.Color;
import com.ProjetVde.CarHive.repository.ColorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ColorService {
    @Autowired
    private ColorRepository colorRepository;

    public Color create(Color color) {
        return colorRepository.save(color);
    }
    public Color update(Color color) {
        return colorRepository.save(color);
    }
    public void delete(Long id) {
        colorRepository.deleteById(id);
    }
    public Color getById(Long id){
        return colorRepository.findById(id).orElse(null);
    }
    public Optional<Color> getByName(String color){
        return colorRepository.findByColor(color).or(() -> colorRepository.findByColor(color.toLowerCase()).or(() -> colorRepository.findByColor(color.toUpperCase())));
    }
}
