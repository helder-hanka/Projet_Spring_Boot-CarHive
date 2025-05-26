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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
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
	private String asJsonString(Object obj) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
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

		//when(voitureService.getById(1L)).thenReturn(voiture); // Simulation du service retournant une voiture existante
		when(voitureService.getById(voiture.getId())).thenReturn(Optional.of(voiture));

		mockMvc.perform(get("/voiture/public/1")) // Effectue une requête GET sur l'endpoint
				.andExpect(status().isOk()) // Vérifie que la réponse a un statut 200 (OK)
				.andExpect(jsonPath("$.id").value(1)) // Vérifie que l'ID retourné est bien 1
				.andExpect(jsonPath("$.immatriculation").value("ABC-123")); // Vérifie que l'immatriculation est correcte
	}
	@Test
	void testGetById_NotFound() throws Exception {
		when(voitureService.getById(1L)).thenReturn(Optional.empty()); // Simulation d'une voiture inexistante

		mockMvc.perform(get("/voiture/public/1")) // Requête GET sur l'endpoint
				.andExpect(status().isBadRequest()) // Vérifie que la réponse est une erreur 400
				.andExpect(content().string("La voiture n'existe pas")); // Vérifie que le message d'erreur est correct
	}

	@Test
	void testCreateVoiture_Success() throws Exception {
		// Création d'un objet de requête voiture
		VoitureRequest voitureRequest = new VoitureRequest();
		voitureRequest.setImmatriculation("XYZ-789");
		voitureRequest.setMarque("Toyota");
		voitureRequest.setModele("Corolla");
		voitureRequest.setAnnee(LocalDate.parse("2022-01-01T00:00:00"));
		voitureRequest.setColor("Rouge");
		voitureRequest.setNomGarage("Mon Garage");
		voitureRequest.setAdresseGarage("123 Rue Principale");
		voitureRequest.setTelephoneGarage("0601020304");

		// Création d'un utilisateur
		User user = new User();
		user.setId(1L);
		user.setEmail("test@example.com");
		user.setPassword("password");
		user.setRole(Role.USER);

		// Création du profil utilisateur
		UserProfile userProfile = new UserProfile();
		userProfile.setId(1L);
		userProfile.setFirstName("Ronaldo");
		userProfile.setLastName("JR");
		userProfile.setUser(user);

		// Création du garage
		Garage garage = new Garage();
		garage.setId(1L);
		garage.setNom(voitureRequest.getNomGarage());
		garage.setAdresse(voitureRequest.getAdresseGarage());
		garage.setTelephone(voitureRequest.getTelephoneGarage());

		// Création de la couleur
		Color color = new Color();
		color.setId(1L);
		color.setColor(voitureRequest.getColor());

		// Création de la voiture
		Voiture voiture = new Voiture();
		voiture.setId(1L);
		voiture.setImmatriculation(voitureRequest.getImmatriculation());
		voiture.setMarque(voitureRequest.getMarque());
		voiture.setModele(voitureRequest.getModele());
		voiture.setAnnee(voitureRequest.getAnnee());
		voiture.setGarage(garage);
		voiture.setColor(color);
		voiture.setUserProfile(userProfile);

		// Mock des services
		lenient().when(garageService.getByName("Mon Garage")).thenReturn(Optional.of(garage));
		lenient().when(colorService.getByName("Rouge")).thenReturn(Optional.of(color));
		lenient().when(userProfileService.getById(1L)).thenReturn(userProfile);
		lenient().when(voitureService.create(any(Voiture.class))).thenReturn(voiture);
		lenient().when(securityUtils.getAuthenticatedUserProfile()).thenAnswer(invocation -> ResponseEntity.ok(userProfile));

		// Exécution de la requête et vérifications
		mockMvc.perform(post("/voiture/create")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(voitureRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.immatriculation").value("XYZ-789"))
				.andExpect(jsonPath("$.marque").value("Toyota"))
				.andExpect(jsonPath("$.modele").value("Corolla"))
				.andExpect(jsonPath("$.annee").value("2022"))
				.andExpect(jsonPath("$.color.color").value("Rouge"))
				.andExpect(jsonPath("$.garage.nom").value("Mon Garage"));
	}

	@Test
	void testDeleteVoiture_Success() throws Exception {
		// ID de la voiture à supprimer
		Long voitureId = 1L;

		// Création d'un utilisateur
		User user = new User();
		user.setId(1L);
		user.setEmail("test@example.com");
		user.setPassword("password");
		user.setRole(Role.USER);

		// Création du profil utilisateur
		UserProfile userProfile = new UserProfile();
		userProfile.setId(1L);
		userProfile.setFirstName("Ronaldo");
		userProfile.setLastName("JR");
		userProfile.setUser(user);

		// Création d'une voiture associée à cet utilisateur
		Voiture voiture = new Voiture();
		voiture.setId(voitureId);
		voiture.setImmatriculation("XYZ-789");
		voiture.setMarque("Toyota");
		voiture.setModele("Corolla");
		voiture.setAnnee(LocalDate.parse("2022-01-01T00:00:00"));
		voiture.setUserProfile(userProfile);

		// Mock du profil utilisateur authentifié
		lenient().when(securityUtils.getAuthenticatedUserProfile()).thenAnswer(invocation -> ResponseEntity.ok(userProfile));

		// Mock de la récupération de la voiture
		lenient().when(voitureService.getById(voitureId)).thenReturn(Optional.of(voiture));

		// Mock de la suppression
		doNothing().when(voitureService).delete(voitureId);

		// Exécution de la requête DELETE et vérification du statut 200 OK
		mockMvc.perform(delete("/voiture/{id}", voitureId)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string("Voiture supprimée avec succès"));

		// Vérification que la méthode delete a bien été appelée une fois avec le bon ID
		verify(voitureService, times(1)).delete(voitureId);
	}

	@Test
	void testPutById_Success() throws Exception {
		// ID de la voiture à mettre à jour
		Long voitureId = 1L;

		// Création d'un utilisateur
		User user = new User();
		user.setId(1L);
		user.setEmail("test@example.com");
		user.setPassword("password");
		user.setRole(Role.USER);

		// Création du profil utilisateur
		UserProfile userProfile = new UserProfile();
		userProfile.setId(1L);
		userProfile.setFirstName("Ronaldo");
		userProfile.setLastName("JR");
		userProfile.setUser(user);

		// Création d'une voiture existante
		Voiture voiture = new Voiture();
		voiture.setId(voitureId);
		voiture.setImmatriculation("XYZ-789");
		voiture.setMarque("Toyota");
		voiture.setModele("Corolla");
		voiture.setAnnee(LocalDate.parse("2022-01-01T00:00:00"));
		voiture.setUserProfile(userProfile);

		// Mock du profil utilisateur authentifié
		lenient().when(securityUtils.getAuthenticatedUserProfile()).thenAnswer(invocation -> ResponseEntity.ok(userProfile));

		// Mock de la récupération de la voiture existante
		lenient().when(voitureService.getById(voitureId)).thenReturn(Optional.of(voiture));

		// Création d'un nouveau VoitureRequest pour la mise à jour
		VoitureRequest voitureRequest = new VoitureRequest();
		voitureRequest.setImmatriculation("ABC-123");
		voitureRequest.setMarque("Honda");
		voitureRequest.setModele("Civic");
		voitureRequest.setAnnee(LocalDate.parse("2023-01-01T00:00:00"));
		voitureRequest.setColor("Red");
		voitureRequest.setNomGarage("Garage A");
		voitureRequest.setAdresseGarage("123 Main Street");
		voitureRequest.setTelephoneGarage("0123456789");

		// Mock des services pour gérer la couleur et le garage
		Color color = new Color();
		color.setColor("Red");
		lenient().when(colorService.getByName("Red")).thenReturn(Optional.of(color));

		Garage garage = new Garage();
		garage.setNom("Garage A");
		lenient().when(garageService.getByName("Garage A")).thenReturn(Optional.of(garage));

		// Mock de la mise à jour de la voiture
		lenient().when(voitureService.update(voiture)).thenReturn(voiture);

		// Exécution de la requête PUT et vérification du statut 200 OK
		mockMvc.perform(put("/voiture/{id}", voitureId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(asJsonString(voitureRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.immatriculation").value("ABC-123"))
				.andExpect(jsonPath("$.marque").value("Honda"))
				.andExpect(jsonPath("$.modele").value("Civic"))
				.andExpect(jsonPath("$.annee").value("2023"))
				.andExpect(jsonPath("$.color.color").value("Red"))
				.andExpect(jsonPath("$.garage.nom").value("Garage A"));

		// Vérification que la méthode update a bien été appelée une fois avec la bonne voiture
		verify(voitureService, times(1)).update(voiture);
	}

	@Test
	void testGetAll_Success() throws Exception {
		// Création d'une liste de voitures
		Voiture voiture1 = new Voiture();
		voiture1.setId(1L);
		voiture1.setImmatriculation("XYZ-789");
		voiture1.setMarque("Toyota");
		voiture1.setModele("Corolla");
		voiture1.setAnnee(LocalDate.parse("2022-01-01T00:00:00"));

		Voiture voiture2 = new Voiture();
		voiture2.setId(2L);
		voiture2.setImmatriculation("ABC-123");
		voiture2.setMarque("Honda");
		voiture2.setModele("Civic");
		voiture2.setAnnee(LocalDate.parse("2023-01-01T00:00:00"));

		List<Voiture> voitures = Arrays.asList(voiture1, voiture2);

		// Mock du service qui retourne la liste des voitures
		lenient().when(voitureService.getAll()).thenReturn(voitures);

		// Exécution de la requête GET pour récupérer toutes les voitures et vérification du statut 200 OK
		mockMvc.perform(get("/voiture/public")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].immatriculation").value("XYZ-789"))
				.andExpect(jsonPath("$[1].immatriculation").value("ABC-123"));

		// Vérification que la méthode getAll a bien été appelée
		verify(voitureService, times(1)).getAll();
	}

	@Test
	void testGetAll_NoContent() throws Exception {
		// Mock du service qui retourne une liste vide de voitures
		lenient().when(voitureService.getAll()).thenReturn(Collections.emptyList());

		// Exécution de la requête GET pour récupérer toutes les voitures et vérification du statut 200 OK
		mockMvc.perform(get("/voiture/public")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string("[]")); // Vérifie que la réponse est une liste vide

		// Vérification que la méthode getAll a bien été appelée
		verify(voitureService, times(1)).getAll();
	}

	@Test
	void testGetAll_InternalServerError() throws Exception {
		// Mock du service qui lance une exception
		lenient().when(voitureService.getAll()).thenThrow(new RuntimeException("Erreur interne"));

		// Exécution de la requête GET et vérification du statut 500 Internal Server Error
		mockMvc.perform(get("/voiture/public")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError())
				.andExpect(content().string("Une erreur est survenue : Erreur interne"));

		// Vérification que la méthode getAll a bien été appelée
		verify(voitureService, times(1)).getAll();
	}

}
