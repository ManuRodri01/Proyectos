package manuRodri.intercambioCartas;

import manuRodri.intercambioCartas.DTO.AuthResponse;
import manuRodri.intercambioCartas.DTO.CreacionOfertaDTO;
import manuRodri.intercambioCartas.DTO.LoginRequestDTO;
import manuRodri.intercambioCartas.DTO.RegisterRequestDTO;
import manuRodri.intercambioCartas.Entity.Oferta;
import manuRodri.intercambioCartas.Entity.Persona;
import manuRodri.intercambioCartas.Entity.estadoOferta;
import manuRodri.intercambioCartas.Entity.rolesPersona;
import manuRodri.intercambioCartas.Repository.PersonaRepository;
import manuRodri.intercambioCartas.Service.AuthService;
import manuRodri.intercambioCartas.Service.OfertaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class OfertaControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthService authService;

    @Autowired
    private PersonaRepository personaRepository;

    @MockBean
    private OfertaService ofertaService;

    @BeforeEach
    void setUp() {
        personaRepository.clear();

        RegisterRequestDTO registroDTO = new RegisterRequestDTO(
                "usuarioOferta",
                "password123",
                rolesPersona.USUARIO);
        authService.register(registroDTO);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void usuarioPuedeCrearOfertaConToken() throws Exception {
        CreacionOfertaDTO ofertaDTO = new CreacionOfertaDTO(1, List.of(2), 5.0f);
        Persona persona = new Persona("usuarioOferta", rolesPersona.USUARIO);
        CreacionOfertaDTO ofertaCreada = new CreacionOfertaDTO(1, List.of(2), 5.0f);

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
        Oferta oferta = new Oferta(null, estadoOferta.EN_ESPERA, LocalDate.now());
        Persona persona = new Persona("usuarioOferta", rolesPersona.USUARIO);
        when(ofertaService.getOferta(1,persona)).thenReturn(oferta);


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
        Oferta oferta = new Oferta(null, estadoOferta.ACEPTADA, LocalDate.now());
        Persona persona = new Persona("usuarioOferta", rolesPersona.USUARIO);

        when(ofertaService.aceptarOferta(1, persona)).thenReturn(oferta);

        AuthResponse authResponse = authService.login(new LoginRequestDTO("usuarioOferta", "password123"));

        mockMvc.perform(MockMvcRequestBuilders.post("/ofertas/1/aceptar")
                        .header("Authorization", "Bearer " + authResponse.token()))
                .andExpect(status().isOk());
    }

    @Test
    void usuarioPuedeRechazarOferta() throws Exception {
        Oferta oferta = new Oferta(null, estadoOferta.RECHAZADA, LocalDate.now());
        Persona persona = new Persona("usuarioOferta", rolesPersona.USUARIO);

        when(ofertaService.rechazarOferta(1, persona)).thenReturn(oferta);

        AuthResponse authResponse = authService.login(new LoginRequestDTO("usuarioOferta", "password123"));

        mockMvc.perform(MockMvcRequestBuilders.post("/ofertas/1/rechazar")
                        .header("Authorization", "Bearer " + authResponse.token()))
                .andExpect(status().isOk());
    }

    @Test
    void usuarioPuedeEliminarOferta() throws Exception {
        Oferta oferta = new Oferta(null, estadoOferta.EN_ESPERA, LocalDate.now());
        Persona persona = new Persona("usuarioOferta", rolesPersona.USUARIO);
        when(ofertaService.borrarOferta(1,persona)).thenReturn(oferta);


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
