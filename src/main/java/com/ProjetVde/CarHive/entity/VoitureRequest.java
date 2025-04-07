package com.ProjetVde.CarHive.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VoitureRequest {
    @NotBlank(message = "L'immatriculation est obligatoire")
    private String immatriculation;

    @NotBlank(message = "La marque est obligatoire")
    private String marque;

    @NotBlank(message = "Le modèle est obligatoire")
    private String modele;

    @NotBlank(message = "L'année est obligatoire")
    private String annee;

    @NotBlank(message = "La couleur est obligatoire")
    private String color;

    @NotBlank(message = "Le nom du garage est obligatoire")
    private String nomGarage;

    @NotBlank(message = "L'adresse du garage est obligatoire")
    private String adresseGarage;

    @NotBlank(message = "Le téléphone du garage est obligatoire")
    private String telephoneGarage;

    @NotBlank(message ="L'image Url est obligatoire")
    private  String imageUrl;
}
