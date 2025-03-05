package com.ProjetVde.CarHive.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 200)
    private String email;

    @Column(nullable = false, length = 200)
    //@JsonIgnore
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // Relation OneToOne avec UserProfile
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "userProfileId")
    private UserProfile userProfile;
}
