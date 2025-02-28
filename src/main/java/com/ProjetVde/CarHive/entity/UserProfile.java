package com.ProjetVde.CarHive.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "user_profile")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String firstName;

    @Column(nullable = false, length = 20)
    private String lastName;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

//    @OneToMany(mappedBy = "userProfile")
//    private List<Voiture> voitures;

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//    @Column(nullable = false, length = 20)
//    private String firstName;
//    @Column(nullable = false, length = 20)
//    private String lastName;
//
////    @OneToOne
////    @JoinColumn(name = "user_id")
////    private User user;
//
//    // Relation OneToMany avec Voiture
//    @OneToMany(mappedBy = "userProfile")
//    private List<Voiture> voitures;
//
//    // Relation OneToOne avec User
//    @OneToOne(mappedBy = "userProfile")
//    private User user;
}
