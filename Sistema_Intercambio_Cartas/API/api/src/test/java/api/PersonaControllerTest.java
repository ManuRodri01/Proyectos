package api;

import api.DTO.AuthResponse;
import api.DTO.LoginRequestDTO;
import api.DTO.RegisterRequestDTO;
import api.Entity.*;
import api.JWT.JwtAuthFilter;
import api.JWT.JwtService;
import api.Repository.OfertaRepository;
import api.Repository.PersonaRepository;
import api.Repository.PublicacionRepository;
import api.Service.AuthService;
import api.Service.CartaService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
class PersonaControllerTest {




    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PersonaRepository personaRepository;

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");

    @MockBean
    private CartaService cartaService;

    @Autowired
    private PublicacionRepository publicacionRepository;

    @Autowired
    private OfertaRepository ofertaRepository;

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @DynamicPropertySource
    static void overrideMongoProps(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @TestConfiguration
    static class Config {

        @Bean
        public PublicacionRepository publicacionRepository() {
            return mock(PublicacionRepository.class);
        }

        @Bean
        public OfertaRepository ofertaRepository() {
            return mock(OfertaRepository.class);
        }
    }


    @BeforeEach
    void setUp() {
        personaRepository.clear();
        RegisterRequestDTO registroDTO = new RegisterRequestDTO(
                "usuarioTest",
                "password123",
                RolesPersona.USUARIO);
        authService.register(registroDTO);
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void usuarioPuedeAccederAEndpointPersonasConToken() throws Exception {

        LoginRequestDTO loginDTO = new LoginRequestDTO("usuarioTest",
                "password123");
        AuthResponse token = authService.login(loginDTO);


        mockMvc.perform(MockMvcRequestBuilders.get("/personas")
                        .header("Authorization", "Bearer " + token.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("usuarioTest"));




    }

    @Test
    public void usuarioNoPuedeAccederAEndpointPersonasSinToken() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/personas"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void usuarioNoPuedeAccederAEndpointPersonasConTokenInvalido() {

        String tokenInvalido = "Bearer token_invalido";


        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", tokenInvalido);


        ResponseEntity<Persona> response = restTemplate
                .exchange("/personas", HttpMethod.GET, new HttpEntity<>(headers), Persona.class);


        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void usuarioNoPuedeAccederAEndpointEstadisticasSinToken() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/estadisticas"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void usuarioNoPuedeAccederAEndpointEstadisticasConTokenInvalido(){

        String tokenInvalido = "Bearer token_invalido";


        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", tokenInvalido);


        ResponseEntity<Persona> response = restTemplate
                .exchange("/estadisticas", HttpMethod.GET, new HttpEntity<>(headers), Persona.class);


        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void usuarioPuedeAccederAEndpointPersonasPublicacionesConToken() throws Exception {

        Carta carta = new Carta("0", "", null);
        List<String> imagenes1 = new ArrayList<>();
        imagenes1.add("imagenTest");
        List<String> imagenes2 = new ArrayList<>();
        imagenes2.add("imagenTest2");
        List<Publicacion> lista = Arrays.asList(
                new Publicacion(
                        LocalDateTime.now(),
                        EstadoConservacion.NUEVA,
                        imagenes1,
                        carta.getId()),
                new Publicacion(
                        LocalDateTime.now(),
                        EstadoConservacion.NUEVA,
                        imagenes2,
                        carta.getId()));

        Persona persona = personaRepository.getPersona("usuarioTest");


        when(publicacionRepository.getPublicacionesByPersona(persona.getId())).thenReturn(lista);
        when(cartaService.getCartaNoDTO(carta.getId())).thenReturn(carta);

        LoginRequestDTO loginDTO = new LoginRequestDTO(
                "usuarioTest",
                "password123");
        AuthResponse authResponse = authService.login(loginDTO);


        mockMvc.perform(MockMvcRequestBuilders.get("/personas/publicaciones")
                        .header("Authorization", "Bearer " + authResponse.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].imagen").value("imagenTest"))
                .andExpect(jsonPath("$[1].imagen").value("imagenTest2"));

    }

    @Test
    public void usuarioNoPuedeAccederAEndpointPersonasPublicacionesSinToken() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/personas/publicaciones"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void usuarioNoPuedeAccederAEndpointPersonasPublicacionesConTokenInvalido() {

        String tokenInvalido = "Bearer token_invalido";


        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", tokenInvalido);


        ResponseEntity<Persona> response = restTemplate
                .exchange("/personas/publicaciones", HttpMethod.GET, new HttpEntity<>(headers), Persona.class);


        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void usuarioPuedeAccederAEndpointPersonasOfertasHechasConToken() throws Exception {

        Carta carta = new Carta("0", "", null);
        List<String> imagenes1 = new ArrayList<>();
        imagenes1.add("imagenTest");
        List<String> imagenes2 = new ArrayList<>();
        imagenes2.add("imagenTest2");

        Publicacion publi1 = new Publicacion(
                        LocalDateTime.now(),
                        EstadoConservacion.NUEVA,
                        imagenes1,
                        carta.getId());
        publi1.setId("1");
        Publicacion publi2 = new Publicacion(
                LocalDateTime.now(),
                EstadoConservacion.NUEVA,
                imagenes2,
                carta.getId());
        publi2.setId("2");

        Persona ofertante = new Persona("ofertanteTest", "contrasenia", RolesPersona.USUARIO);

        List<Oferta> lista = Arrays.asList(
                new Oferta(
                        EstadoOferta.EN_ESPERA,
                        publi1.getId(),
                        LocalDate.now(),
                        ofertante.getId()
                ),
                new Oferta(
                        EstadoOferta.EN_ESPERA,
                        publi2.getId(),
                        LocalDate.now(),
                        ofertante.getId()
                ));

        Persona persona = personaRepository.getPersona("usuarioTest");

        when(ofertaRepository.getOfertasHechasByPersona(persona.getId())).thenReturn(lista);
        when(cartaService.getCartaNoDTO(carta.getId())).thenReturn(carta);
        when(publicacionRepository.getPublicacion("1")).thenReturn(publi1);
        when(publicacionRepository.getPublicacion("2")).thenReturn(publi2);

        LoginRequestDTO loginDTO = new LoginRequestDTO(
                "usuarioTest",
                "password123");
        AuthResponse authResponse = authService.login(loginDTO);


        mockMvc.perform(MockMvcRequestBuilders.get("/personas/ofertas-hechas")
                        .header("Authorization", "Bearer " + authResponse.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].imagen").value("imagenTest"))
                .andExpect(jsonPath("$[1].imagen").value("imagenTest2"));

    }

    @Test
    public void usuarioNoPuedeAccederAEndpointPersonasOfertasHechasSinToken() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/personas/ofertas-hechas"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void usuarioNoPuedeAccederAEndpointPersonasOfertasHechasConTokenInvalido() {

        String tokenInvalido = "Bearer token_invalido";


        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", tokenInvalido);


        ResponseEntity<Persona> response = restTemplate
                .exchange("/personas/ofertas-hechas", HttpMethod.GET, new HttpEntity<>(headers), Persona.class);


        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void usuarioPuedeAccederAEndpointPersonasOfertasRecibidasConToken() throws Exception {

        Carta carta = new Carta("0", "", null);
        List<String> imagenes1 = new ArrayList<>();
        imagenes1.add("imagenTest");
        List<String> imagenes2 = new ArrayList<>();
        imagenes2.add("imagenTest2");

        Publicacion publi1 = new Publicacion(
                LocalDateTime.now(),
                EstadoConservacion.NUEVA,
                imagenes1,
                carta.getId());
        publi1.setId("1");
        Publicacion publi2 = new Publicacion(
                LocalDateTime.now(),
                EstadoConservacion.NUEVA,
                imagenes2,
                carta.getId());
        publi2.setId("2");

        Persona ofertante = new Persona("ofertanteTest", "contrasenia", RolesPersona.USUARIO);

        List<Oferta> lista = Arrays.asList(
                new Oferta(
                        EstadoOferta.EN_ESPERA,
                        publi1.getId(),
                        LocalDate.now(),
                        ofertante.getId()
                ),
                new Oferta(
                        EstadoOferta.EN_ESPERA,
                        publi2.getId(),
                        LocalDate.now(),
                        ofertante.getId()
                ));

        Persona persona = personaRepository.getPersona("usuarioTest");

        when(ofertaRepository.getOfertasRecibidasByPersona(persona.getId())).thenReturn(lista);
        when(cartaService.getCartaNoDTO(carta.getId())).thenReturn(carta);
        when(publicacionRepository.getPublicacion("1")).thenReturn(publi1);
        when(publicacionRepository.getPublicacion("2")).thenReturn(publi2);

        LoginRequestDTO loginDTO = new LoginRequestDTO(
                "usuarioTest",
                "password123");
        AuthResponse authResponse = authService.login(loginDTO);


        mockMvc.perform(MockMvcRequestBuilders.get("/personas/ofertas-recibidas")
                        .header("Authorization", "Bearer " + authResponse.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].imagen").value("imagenTest"))
                .andExpect(jsonPath("$[1].imagen").value("imagenTest2"));

    }

    @Test
    public void usuarioNoPuedeAccederAEndpointPersonasOfertasRecibidasSinToken() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/personas/ofertas-recibidas"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void usuarioNoPuedeAccederAEndpointPersonasOfertasRecibidasConTokenInvalido() {

        String tokenInvalido = "Bearer token_invalido";


        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", tokenInvalido);


        ResponseEntity<Persona> response = restTemplate
                .exchange("/personas/ofertas-recibidas", HttpMethod.GET, new HttpEntity<>(headers), Persona.class);


        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void usuarioNoPuedeAccederConTokenExpirado(){

        Persona persona = new Persona("usuarioTest", RolesPersona.USUARIO);

        String tokenVencido = jwtService.generarTokenExpirado(persona);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + tokenVencido);

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        assertThrows(ExpiredJwtException.class, () -> {
            jwtAuthFilter.doFilter(request, response, chain);
        });
    }






}
