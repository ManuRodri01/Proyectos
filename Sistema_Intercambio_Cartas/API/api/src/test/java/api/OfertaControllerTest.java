package api;


import api.DTO.*;
import api.Entity.EstadoOferta;
import api.Entity.Oferta;
import api.Entity.Persona;
import api.Entity.RolesPersona;
import api.Repository.OfertaRepository;
import api.Repository.PersonaRepository;
import api.Service.AuthService;
import api.Service.OfertaMapper;
import api.Service.OfertaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
public class OfertaControllerTest {



    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthService authService;

    @Autowired
    private PersonaRepository personaRepository;

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OfertaMapper ofertaMapper;

    @MockBean
    private OfertaService ofertaService;

    @MockBean
    private OfertaRepository ofertaRepository;

    @DynamicPropertySource
    static void overrideMongoProps(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeEach
    void setUp() {
        personaRepository.clear();

        RegisterRequestDTO registroDTO = new RegisterRequestDTO(
                "usuarioOferta",
                "password123",
                RolesPersona.USUARIO);
        authService.register(registroDTO);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void usuarioPuedeCrearOfertaConToken() throws Exception {
        CreacionOfertaDTO ofertaDTO = new CreacionOfertaDTO("1", List.of("2"), 5.0f);
        CreacionOfertaDTO ofertaCreada = new CreacionOfertaDTO("1", List.of("2"), 5.0f);
        Persona persona = new Persona("usuarioOferta",
                passwordEncoder.encode("password123"),
                RolesPersona.USUARIO);
        persona.setId("1");
        when(ofertaService.guardarOferta(ofertaDTO, persona)).thenReturn(ofertaCreada);

        AuthResponse authResponse = authService.login(new LoginRequestDTO("usuarioOferta", "password123"));

        mockMvc.perform(MockMvcRequestBuilders.post("/ofertas")
                        .header("Authorization", "Bearer " + authResponse.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idPublicacion\":1,\"idOferta\":2}"))
                .andExpect(status().isOk());
    }

    @Test
    void usuarioPuedeObtenerOfertaConToken() throws Exception {
        RespuestaOfertaDTO oferta = new RespuestaOfertaDTO(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
        Persona persona = new Persona("usuarioOferta",
                passwordEncoder.encode("password123"),
                RolesPersona.USUARIO);
        persona.setId("1");
        when(ofertaService.getOferta("1",persona)).thenReturn(oferta);


        AuthResponse authResponse = authService.login(new LoginRequestDTO("usuarioOferta", "password123"));

        mockMvc.perform(MockMvcRequestBuilders.get("/ofertas/1")
                        .header("Authorization", "Bearer " + authResponse.token()))
                .andExpect(status().isOk());
    }

    @Test
    void usuarioNoPuedeAccederASinToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/ofertas/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void usuarioPuedeAceptarOferta() throws Exception {
        Oferta oferta = new Oferta(null, EstadoOferta.ACEPTADA, LocalDate.now());
        Persona persona = new Persona("usuarioOferta",
                passwordEncoder.encode("password123"),
                RolesPersona.USUARIO);
        persona.setId("1");

        when(ofertaService.aceptarOferta("1", persona)).thenReturn(oferta);

        AuthResponse authResponse = authService.login(new LoginRequestDTO("usuarioOferta", "password123"));

        mockMvc.perform(MockMvcRequestBuilders.post("/ofertas/1/aceptar")
                        .header("Authorization", "Bearer " + authResponse.token()))
                .andExpect(status().isOk());
    }

    @Test
    void usuarioPuedeRechazarOferta() throws Exception {
        Oferta oferta = new Oferta(null, EstadoOferta.RECHAZADA, LocalDate.now());
        Persona persona = new Persona("usuarioOferta",
                passwordEncoder.encode("password123"),
                RolesPersona.USUARIO);
        persona.setId("1");

        when(ofertaService.rechazarOferta("1", persona)).thenReturn(oferta);

        AuthResponse authResponse = authService.login(new LoginRequestDTO("usuarioOferta", "password123"));

        mockMvc.perform(MockMvcRequestBuilders.post("/ofertas/1/rechazar")
                        .header("Authorization", "Bearer " + authResponse.token()))
                .andExpect(status().isOk());
    }

    @Test
    void usuarioPuedeEliminarOferta() throws Exception {
        Oferta oferta = new Oferta(null, EstadoOferta.EN_ESPERA, LocalDate.now());
        Persona persona = new Persona("usuarioOferta",
                passwordEncoder.encode("password123"),
                RolesPersona.USUARIO);
        persona.setId("1");

        when(ofertaService.borrarOferta("1",persona)).thenReturn(oferta);


        AuthResponse authResponse = authService.login(new LoginRequestDTO("usuarioOferta", "password123"));

        mockMvc.perform(MockMvcRequestBuilders.delete("/ofertas/1")
                        .header("Authorization", "Bearer " + authResponse.token()))
                .andExpect(status().isOk());
    }

    @Test
    public void usuarioNoPuedeAccederConTokenInvalido() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer token_invalido");

        ResponseEntity<String> response = restTemplate
                .exchange("/ofertas/1", HttpMethod.GET, new HttpEntity<>(headers), String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }


}
