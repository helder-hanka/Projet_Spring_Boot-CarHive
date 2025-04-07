package com.ProjetVde.CarHive.entity;

import jakarta.persistence.*;
import lombok.Data;
@Data
@Entity
@Table(name = "voiture")
public class Voiture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true , nullable = false, length = 10)
    private String immatriculation;

    @Column(nullable = false, length = 20)
    private String marque;

    @Column(nullable = false, length = 20)
    private String modele;

    @Column(nullable = false, length = 10, columnDefinition = "DATE")
    private String annee;

    @Column(length = 300)
    private  String imageUrl;

    @ManyToOne
    @JoinColumn(name = "garage_id", nullable= false)
    private Garage garage;

    @ManyToOne
    @JoinColumn(name = "color_id", nullable = false)
    private Color color;

    @ManyToOne
    @JoinColumn(name = "user_profile_id", nullable = false)
    private UserProfile userProfile;
}
