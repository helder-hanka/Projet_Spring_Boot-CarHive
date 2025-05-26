package com.ProjetVde.CarHive.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class VoitureRequest {
    @NotBlank(message = "L'immatriculation est obligatoire")
    private String immatriculation;

    @NotBlank(message = "La marque est obligatoire")
    private String marque;

    @NotBlank(message = "Le modèle est obligatoire")
    private String modele;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "L'année est obligatoire")
    private LocalDate annee;

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
