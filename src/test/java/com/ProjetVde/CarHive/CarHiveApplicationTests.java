package com.ProjetVde.CarHive;

import com.ProjetVde.CarHive.controller.VoitureController;
import com.ProjetVde.CarHive.entity.*;
import com.ProjetVde.CarHive.service.*;
import com.ProjetVde.CarHive.utils.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class CarHiveApplicationTests {
	private MockMvc mockMvc;
	@Mock
	VoitureService voitureService;
	@Mock
	private GarageService garageService;
	@Mock
	private UserProfileService userProfileService;
	@Mock
	private ColorService colorService;
	@Mock
	private SecurityUtils securityUtils;
	@InjectMocks
	private VoitureController voitureController;
	private ObjectMapper objectMapper;
	@Test
	void contextLoads() {
	}

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(voitureController).build(); // Initialise MockMvc avec le contrôleur
		objectMapper = new ObjectMapper(); // Initialise l'ObjectMapper pour la conversion JSON
	}
	@Test
	void testGetById_Success() throws Exception {
		Voiture voiture = new Voiture(); // Création d'un objet Voiture fictif
		voiture.setId(1L);
		voiture.setImmatriculation("ABC-123");

		when(voitureService.getById(1L)).thenReturn(voiture); // Simulation du service retournant une voiture existante

		mockMvc.perform(get("/voiture/public/1")) // Effectue une requête GET sur l'endpoint
				.andExpect(status().isOk()) // Vérifie que la réponse a un statut 200 (OK)
				.andExpect(jsonPath("$.id").value(1)) // Vérifie que l'ID retourné est bien 1
				.andExpect(jsonPath("$.immatriculation").value("ABC-123")); // Vérifie que l'immatriculation est correcte
	}
	@Test
	void testGetById_NotFound() throws Exception {
		when(voitureService.getById(1L)).thenReturn(null); // Simulation d'une voiture inexistante

		mockMvc.perform(get("/voiture/public/1")) // Requête GET sur l'endpoint
				.andExpect(status().isBadRequest()) // Vérifie que la réponse est une erreur 400
				.andExpect(content().string("La voiture n'existe pas")); // Vérifie que le message d'erreur est correct
	}

	@Test
	void testCreateVoiture_Success() throws Exception {
		VoitureRequest voitureRequest = new VoitureRequest();
		voitureRequest.setImmatriculation("XYZ-789");
		voitureRequest.setMarque("Toyota");
		voitureRequest.setModele("Corolla");
		voitureRequest.setAnnee("2022");
		voitureRequest.setColor("Rouge");
		voitureRequest.setNomGarage("Mon Garage");
		voitureRequest.setAdresseGarage("123 Rue Principale");
		voitureRequest.setTelephoneGarage("0601020304");

		User user = new User();
		user.setId(1L);
		user.setEmail("test@example.com");
		user.setPassword("password");
		user.setRole(Role.USER);

		Garage garage = new Garage();
		garage.setId(1L);
		garage.setNom(voitureRequest.getNomGarage());
		garage.setAdresse(voitureRequest.getAdresseGarage());
		garage.setTelephone(voitureRequest.getTelephoneGarage());

		Color color = new Color();
		color.setId(1L);
		color.setColor(voitureRequest.getColor());

		UserProfile userProfile = new UserProfile();
		userProfile.setId(1L);
		userProfile.setFirstName("Ronaldo");
		userProfile.setLastName("JR");
		userProfile.setUser(user);

		Voiture voiture = new Voiture();
		voiture.setId(1L);
		voiture.setImmatriculation(voitureRequest.getImmatriculation());
		voiture.setMarque(voitureRequest.getMarque());
		voiture.setModele(voitureRequest.getModele());
		voiture.setAnnee(voitureRequest.getAnnee());
		voiture.setGarage(garage);
		voiture.setColor(color);
		voiture.setUserProfile(userProfile);

		when(garageService.getByName("Mon Garage")).thenReturn(Optional.of(garage));
		when(colorService.getByName("Rouge")).thenReturn(Optional.of(color));
		when(userProfileService.getById(1L)).thenReturn(userProfile);
		when(voitureService.create(any(Voiture.class))).thenReturn(voiture);

		mockMvc.perform(post("/voiture/create")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(voitureRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.immatriculation").value("XYZ-789"));
	}

//	@Test
//	void testDeleteVoiture_Success() throws Exception {
//		Voiture voiture = new Voiture(); // Création d'une voiture fictive
//		voiture.setId(1L);
//
//		when(voitureService.getById(1L)).thenReturn(voiture); // Simulation de la récupération d'une voiture existante
//		Mockito.doNothing().when(voitureService).delete(1L); // Simulation de la suppression
//
//		mockMvc.perform(delete("/voiture/1")) // Effectue une requête DELETE
//				.andExpect(status().isOk()) // Vérifie que la réponse est 200 (OK)
//				.andExpect(content().string("Voiture supprimée avec succès")); // Vérifie le message de confirmation
//	}

}
