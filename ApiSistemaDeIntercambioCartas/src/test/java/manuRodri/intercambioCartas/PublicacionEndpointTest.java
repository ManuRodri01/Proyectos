package manuRodri.intercambioCartas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import manuRodri.intercambioCartas.DTO.*;
import manuRodri.intercambioCartas.Entity.Carta;
import manuRodri.intercambioCartas.Entity.Publicacion;
import manuRodri.intercambioCartas.Entity.estadoConservacion;
import manuRodri.intercambioCartas.Entity.rolesPersona;
import manuRodri.intercambioCartas.JWT.JwtAuthFilter;
import manuRodri.intercambioCartas.JWT.JwtService;
import manuRodri.intercambioCartas.Repository.OfertaRepository;
import manuRodri.intercambioCartas.Repository.PersonaRepository;
import manuRodri.intercambioCartas.Repository.PublicacionRepository;
import manuRodri.intercambioCartas.Service.AuthService;
import manuRodri.intercambioCartas.Service.PublicacionService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class PublicacionEndpointTest {
	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private AuthService authService;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private PersonaRepository personaRepository;

	@Autowired
	private PublicacionRepository publicacionRepository;

	@Autowired
	private PublicacionService publicacionService;

	@Autowired
	private OfertaRepository ofertaRepository;

	@Autowired
	private JwtAuthFilter jwtAuthFilter;

	@TestConfiguration
	static class Config {

		@Bean
		public PublicacionRepository publicacionRepository() {
			return mock(PublicacionRepository.class);
		}



		@Bean
		public OfertaRepository ofertaRepository(){
			return mock(OfertaRepository.class);
		}
	}


	@BeforeEach
	void setUp() {
		personaRepository.clear();
		RegisterRequestDTO registroDTO = new RegisterRequestDTO(
				"usuarioTest",
				"password123",
				rolesPersona.USUARIO);
		authService.register(registroDTO);
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void usuarioPuedeAccederAEndpointPublicacionConToken() throws Exception{
		LoginRequestDTO loginDTO = new LoginRequestDTO("usuarioTest",
				"password123");
		AuthResponse token = authService.login(loginDTO);

		Carta carta = new Carta(0, "", null);
		List<String> imagenes1 = new ArrayList<>();
		imagenes1.add("imagenTest1");
		List<String> imagenes2 = new ArrayList<>();
		imagenes2.add("imagenTest2");

		List<Publicacion> publicaciones = Arrays.asList(
				new Publicacion(
						LocalDateTime.now(),
						estadoConservacion.NUEVA,
						imagenes1,
						carta
				),
				new Publicacion(
						LocalDateTime.now(),
						estadoConservacion.NUEVA,
						imagenes2,
						carta
				)
		);
		FiltroPublicacionesDTO filtros = new FiltroPublicacionesDTO(null,null,null,null,null,null,null,null,null);
		when(publicacionRepository.getPublicaciones(filtros)).thenReturn(publicaciones);

		mockMvc.perform(MockMvcRequestBuilders.get("/publicaciones")
						.header("Authorization", "Bearer " + token.token())
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"cartaId\":null,\"juegoId\":null,\"nombrePublicacion\":null, \"estadoConservacion\":null,\"desdeFecha\":null,\"hastaFecha\":null, \"desdePrecio\":null,\"hastaPrecio\":null, \"intereses\":null }"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].imagenes").value("imagenTest1"))
				.andExpect(jsonPath("$[1].imagenes").value("imagenTest2"));

	}

	@Test
	public void usuarioNoPuedeAccederAEndpointPublicacionesConTokenInvalido() {

		String tokenInvalido = "Bearer token_invalido";


		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", tokenInvalido);


		ResponseEntity<Publicacion> response = restTemplate
				.exchange("/publicaciones", HttpMethod.GET, new HttpEntity<>(headers), Publicacion.class);


		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
	}

	@Test
	public void usuarioPuedeAccederAEndpointGetPublicacionConToken() throws Exception{
		LoginRequestDTO loginDTO = new LoginRequestDTO("usuarioTest",
				"password123");
		AuthResponse token = authService.login(loginDTO);

		Carta carta = new Carta(0, "", null);
		List<String> imagenes1 = new ArrayList<>();
		imagenes1.add("imagenTest1");
		List<String> imagenes2 = new ArrayList<>();
		imagenes2.add("imagenTest2");

		List<Publicacion> publicaciones = Arrays.asList(
				new Publicacion(
						LocalDateTime.now(),
						estadoConservacion.NUEVA,
						imagenes1,
						carta
				),
				new Publicacion(
						LocalDateTime.now(),
						estadoConservacion.NUEVA,
						imagenes2,
						carta
				)
		);

		when(publicacionRepository.getPublicacion(0)).thenReturn(publicaciones.get(0));
		when(publicacionRepository.existePublicacion(0)).thenReturn(true);

		mockMvc.perform(MockMvcRequestBuilders.get("/publicaciones/0")
						.header("Authorization", "Bearer " + token.token()))
				.andExpect(status().isOk());
	}

	@Test
	void usuarioPuedeCrearPublicionConToken() throws Exception {
		LoginRequestDTO loginDTO = new LoginRequestDTO("usuarioTest",
				"password123");
		AuthResponse token = authService.login(loginDTO);

		CreacionPublicacionDTO publicacionDTO = new CreacionPublicacionDTO(0,
				"",
				List.of(0),
				List.of(""),
				(float) 0,
				estadoConservacion.NUEVA);

		Carta carta = new Carta(0, "", null);
		List<String> imagenes1 = new ArrayList<>();
		imagenes1.add("imagenTest1");
		Publicacion publicacion = new Publicacion(
				LocalDateTime.now(),
				estadoConservacion.NUEVA,
				imagenes1,
				carta
		);

		when(publicacionRepository.guardarPublicacion(publicacion)).thenReturn(publicacion);

		mockMvc.perform(MockMvcRequestBuilders.post("/publicaciones")
						.header("Authorization", "Bearer " + token.token())
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"cartaId\":0,\"descripcion\":\"hola\",\"intereses\":[0],\"imagenes\": [\"hola\"],\"precio\":0,\"estadoConservacion\":0}"))
				.andExpect(status().isOk());
	}

	@Test
	void usuarioNOPuedeCrearPublicionSinToken() throws Exception {
		String tokenInvalido = "Bearer token_invalido";


		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", tokenInvalido);


		ResponseEntity<Publicacion> response = restTemplate
				.exchange("/publicaciones", HttpMethod.POST, new HttpEntity<>(headers), Publicacion.class);


		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
	}
}
