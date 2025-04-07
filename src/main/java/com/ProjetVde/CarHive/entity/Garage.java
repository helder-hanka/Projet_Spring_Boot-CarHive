package com.ProjetVde.CarHive.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "garage")
public class Garage {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false, length = 100)
    private String nom;
    @Column(nullable = false, length = 100)
    private String adresse;
    @Column(nullable = false, length = 100)
    private String telephone;

    // Relation OneToMany avec Voiture
//    @OneToMany(mappedBy = "garage")
//    private List<Voiture> voitures;

//    @ManyToMany(mappedBy = "garages")
//    @JoinColumn(name = "voiture_id")
//    private Voiture voitures;
}
