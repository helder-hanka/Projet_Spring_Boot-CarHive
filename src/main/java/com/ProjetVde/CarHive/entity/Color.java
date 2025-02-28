package com.ProjetVde.CarHive.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Table(name = "color")
@Entity
public class Color {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private String color;

    // Relation OneToMany avec Voiture
//    @OneToMany(mappedBy = "color")
//    private List<Voiture> voitures;
}
