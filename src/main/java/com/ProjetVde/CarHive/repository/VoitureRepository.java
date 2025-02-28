package com.ProjetVde.CarHive.repository;

import com.ProjetVde.CarHive.entity.Voiture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoitureRepository extends JpaRepository<Voiture, Long> {
}