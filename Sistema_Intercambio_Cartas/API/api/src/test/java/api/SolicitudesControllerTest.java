package api;

import api.DTO.AuthResponse;
import api.DTO.CreacionJuegoDTO;
import api.DTO.LoginRequestDTO;
import api.DTO.RegisterRequestDTO;
import api.Entity.Carta;
import api.Entity.EstadoSolicitud;
import api.Entity.Juego;
import api.Entity.RolesPersona;
import api.Repository.*;
import api.Service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
public class SolicitudesControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private CartaRepository cartaRepository;

    @Autowired
    private JuegoRepository juegoRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private SolicitudCartaRepository solicitudCartaRepository;

    @Autowired
    private SolicitudJuegoRepository solicitudJuegoRepository;

    @Autowired
    private MockMvc mockMvc;

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");


    @DynamicPropertySource
    static void overrideMongoProps(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeEach
    public void setUp() {
        personaRepository.clear();
        cartaRepository.clear();
        juegoRepository.clear();
        solicitudJuegoRepository.clear();
        solicitudCartaRepository.clear();
        RegisterRequestDTO registroDTO = new RegisterRequestDTO(
                "usuarioTest",
                "password123",
                RolesPersona.USUARIO);

        RegisterRequestDTO registro2DTO = new RegisterRequestDTO(
                "usuario2Test",
                "password123",
                RolesPersona.USUARIO);

        RegisterRequestDTO registro3DTO = new RegisterRequestDTO(
                "adminTest",
                "password123",
                RolesPersona.ADMIN);

        authService.register(registroDTO);
        authService.register(registro2DTO);
        authService.register(registro3DTO);

        CreacionJuegoDTO juegoDTO = new CreacionJuegoDTO("Pokemon");
        juegoRepository.guardarJuego(juegoDTO);

        Carta carta = new Carta();
        carta.setNombre("Pikachu EX");
        carta.setJuegoId(juegoRepository.getTodos().get(0).getId());
        cartaRepository.guardarCarta(carta);
    }

    @Test
    public void usuarioCreaSolicitudDeJuego() throws Exception {
        LoginRequestDTO loginDTO = new LoginRequestDTO("usuarioTest",
                "password123");
        AuthResponse token = authService.login(loginDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/solicitudes/juegos")
                        .header("Authorization", "Bearer " + token.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombreJuego\":\"Digimon\"}"))
                .andExpect(status().isOk());

        assertEquals("Digimon", solicitudJuegoRepository.obtenerTodasSolicitudes().get(0).getNombreJuego());
    }

    @Test
    public void usuarioNoPuedeCrearSolicitudJuegoYaExistente() throws Exception {
        LoginRequestDTO loginDTO = new LoginRequestDTO("usuarioTest",
                "password123");
        AuthResponse token = authService.login(loginDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/solicitudes/juegos")
                        .header("Authorization", "Bearer " + token.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombreJuego\":\"Pokemon\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    public void usuarioNoPuedeCrearSolicitudJuegoConExcesoCaracteres() throws Exception {
        LoginRequestDTO loginDTO = new LoginRequestDTO("usuarioTest",
                "password123");
        AuthResponse token = authService.login(loginDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/solicitudes/juegos")
                        .header("Authorization", "Bearer " + token.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombreJuego\":\"La leyenda del reino perdido: sombras del destino eterno\"}"))
                .andExpect(status().isPayloadTooLarge());
    }

    @Test
    public void usuarioNoPuedeCrearMasDeTresSolicitudesJuegoPorDia() throws Exception {
        LoginRequestDTO loginDTO = new LoginRequestDTO("usuarioTest",
                "password123");
        AuthResponse token = authService.login(loginDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/solicitudes/juegos")
                        .header("Authorization", "Bearer " + token.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombreJuego\":\"Digimon\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.post("/solicitudes/juegos")
                        .header("Authorization", "Bearer " + token.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombreJuego\":\"Magic\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.post("/solicitudes/juegos")
                        .header("Authorization", "Bearer " + token.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombreJuego\":\"Yugi\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.post("/solicitudes/juegos")
                        .header("Authorization", "Bearer " + token.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombreJuego\":\"Dragon Ball\"}"))
                .andExpect(status().isTooManyRequests());
    }

    @Test
    public void usuarioYAdminPuedenAccederASolicitudJuego() throws Exception {
        LoginRequestDTO loginDTO = new LoginRequestDTO("usuarioTest",
                "password123");
        AuthResponse tokenUsuario1 = authService.login(loginDTO);

        LoginRequestDTO login2DTO = new LoginRequestDTO("adminTest",
                "password123");
        AuthResponse tokenAdmin = authService.login(login2DTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/solicitudes/juegos")
                        .header("Authorization", "Bearer " + tokenUsuario1.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombreJuego\":\"Digimon\"}"))
                .andExpect(status().isOk());

        String idSolicitud = solicitudJuegoRepository.obtenerTodasSolicitudes().get(0).getId();

        mockMvc.perform(MockMvcRequestBuilders.get("/solicitudes/juegos/" + idSolicitud)
                        .header("Authorization", "Bearer " + tokenAdmin.token()))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/solicitudes/juegos/" + idSolicitud)
                        .header("Authorization", "Bearer " + tokenUsuario1.token()))
                .andExpect(status().isOk());
    }

    @Test
    public void usuarioExternoNoPuedeAccederASolicitudJuego() throws Exception {
        LoginRequestDTO loginDTO = new LoginRequestDTO("usuarioTest",
                "password123");
        AuthResponse tokenUsuario1 = authService.login(loginDTO);

        LoginRequestDTO login2DTO = new LoginRequestDTO("usuario2Test",
                "password123");
        AuthResponse tokenUsuario2 = authService.login(login2DTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/solicitudes/juegos")
                        .header("Authorization", "Bearer " + tokenUsuario1.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombreJuego\":\"Digimon\"}"))
                .andExpect(status().isOk());

        String idSolicitud = solicitudJuegoRepository.obtenerTodasSolicitudes().get(0).getId();

        mockMvc.perform(MockMvcRequestBuilders.get("/solicitudes/juegos/" + idSolicitud)
                        .header("Authorization", "Bearer " + tokenUsuario2.token()))
                .andExpect(status().isForbidden());


    }

    @Test
    public void usuarioPuedeAccederATodasSusSolicitudesJuego() throws Exception {
        LoginRequestDTO loginDTO = new LoginRequestDTO("usuarioTest",
                "password123");
        AuthResponse tokenUsuario1 = authService.login(loginDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/solicitudes/juegos")
                        .header("Authorization", "Bearer " + tokenUsuario1.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombreJuego\":\"Digimon\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.post("/solicitudes/juegos")
                        .header("Authorization", "Bearer " + tokenUsuario1.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombreJuego\":\"Yugi\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/solicitudes/juegos/persona")
                        .header("Authorization", "Bearer " + tokenUsuario1.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));


    }

    @Test
    public void adminAceptaSolicitudJuego() throws Exception {
        LoginRequestDTO loginDTO = new LoginRequestDTO("usuarioTest",
                "password123");
        AuthResponse tokenUsuario1 = authService.login(loginDTO);

        LoginRequestDTO login2DTO = new LoginRequestDTO("adminTest",
                "password123");
        AuthResponse tokenAdmin = authService.login(login2DTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/solicitudes/juegos")
                        .header("Authorization", "Bearer " + tokenUsuario1.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombreJuego\":\"Digimon\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.post("/solicitudes/juegos/"+ solicitudJuegoRepository.obtenerTodasSolicitudes().get(0).getId() +"/estado-aprobacion")
                        .header("Authorization", "Bearer " + tokenAdmin.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"APROBADA\"}"))
                .andExpect(status().isOk());

        assertEquals(EstadoSolicitud.APROBADA, solicitudJuegoRepository.obtenerTodasSolicitudes().get(0).getEstado());

        List<Juego> juegos = juegoRepository.getTodos();

        assertTrue(juegos.stream().anyMatch(juegoLista -> juegoLista.getNombre().equals("Digimon")));
    }

    @Test
    public void adminAceptaSolicitudJuegoYSeRechazaAutomaticamenteOtra() throws Exception {
        LoginRequestDTO loginDTO = new LoginRequestDTO("usuarioTest",
                "password123");
        AuthResponse tokenUsuario1 = authService.login(loginDTO);

        LoginRequestDTO login2DTO = new LoginRequestDTO("adminTest",
                "password123");
        AuthResponse tokenAdmin = authService.login(login2DTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/solicitudes/juegos")
                        .header("Authorization", "Bearer " + tokenUsuario1.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombreJuego\":\"Digimon\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.post("/solicitudes/juegos")
                        .header("Authorization", "Bearer " + tokenUsuario1.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombreJuego\":\"Digimon\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.post("/solicitudes/juegos/"+ solicitudJuegoRepository.obtenerTodasSolicitudes().get(0).getId() +"/estado-aprobacion")
                        .header("Authorization", "Bearer " + tokenAdmin.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"APROBADA\"}"))
                .andExpect(status().isOk());

        assertEquals(EstadoSolicitud.APROBADA, solicitudJuegoRepository.obtenerTodasSolicitudes().get(0).getEstado());
        assertEquals(EstadoSolicitud.RECHAZADA, solicitudJuegoRepository.obtenerTodasSolicitudes().get(1).getEstado());
    }

    @Test
    public void adminRechazaSolicitudJuego() throws Exception {
        LoginRequestDTO loginDTO = new LoginRequestDTO("usuarioTest",
                "password123");
        AuthResponse tokenUsuario1 = authService.login(loginDTO);

        LoginRequestDTO login2DTO = new LoginRequestDTO("adminTest",
                "password123");
        AuthResponse tokenAdmin = authService.login(login2DTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/solicitudes/juegos")
                        .header("Authorization", "Bearer " + tokenUsuario1.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombreJuego\":\"Digimon\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.post("/solicitudes/juegos/"+ solicitudJuegoRepository.obtenerTodasSolicitudes().get(0).getId() +"/estado-aprobacion")
                        .header("Authorization", "Bearer " + tokenAdmin.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"RECHAZADA\"}"))
                .andExpect(status().isOk());

        assertEquals(EstadoSolicitud.RECHAZADA, solicitudJuegoRepository.obtenerTodasSolicitudes().get(0).getEstado());
    }

    @Test
    public void adminIngresaMalEstadoJuego() throws Exception {
        LoginRequestDTO loginDTO = new LoginRequestDTO("usuarioTest",
                "password123");
        AuthResponse tokenUsuario1 = authService.login(loginDTO);

        LoginRequestDTO login2DTO = new LoginRequestDTO("adminTest",
                "password123");
        AuthResponse tokenAdmin = authService.login(login2DTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/solicitudes/juegos")
                        .header("Authorization", "Bearer " + tokenUsuario1.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombreJuego\":\"Digimon\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.post("/solicitudes/juegos/"+ solicitudJuegoRepository.obtenerTodasSolicitudes().get(0).getId() +"/estado-aprobacion")
                        .header("Authorization", "Bearer " + tokenAdmin.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"ESTADO ERRONEO\"}"))
                .andExpect(status().isBadRequest());

    }



    @Test
    public void usuarioCreaSolicitudDeCarta() throws Exception {
        LoginRequestDTO loginDTO = new LoginRequestDTO("usuarioTest",
                "password123");
        AuthResponse token = authService.login(loginDTO);

        Juego juego = juegoRepository.findOneByNombre("Pokemon");

        mockMvc.perform(MockMvcRequestBuilders.post("/solicitudes/cartas")
                        .header("Authorization", "Bearer " + token.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombreCarta\":\"Charizard EX\",\"idJuego\":\"" + juego.getId() + "\" }"))
                .andExpect(status().isOk());

        assertEquals("Charizard EX", solicitudCartaRepository.obtenerTodasSolicitudes().get(0).getNombreCarta());
    }

    @Test
    public void usuarioNoPuedeCrearSolicitudCartaYaExistente() throws Exception {
        LoginRequestDTO loginDTO = new LoginRequestDTO("usuarioTest",
                "password123");
        AuthResponse token = authService.login(loginDTO);

        Juego juego = juegoRepository.findOneByNombre("Pokemon");

        mockMvc.perform(MockMvcRequestBuilders.post("/solicitudes/cartas")
                        .header("Authorization", "Bearer " + token.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombreCarta\":\"Pikachu EX\",\"idJuego\":\"" + juego.getId() + "\" }"))
                .andExpect(status().isConflict());
    }

    @Test
    public void usuarioNoPuedeCrearSolicitudCartaConJueggoInexistente() throws Exception {
        LoginRequestDTO loginDTO = new LoginRequestDTO("usuarioTest",
                "password123");
        AuthResponse token = authService.login(loginDTO);



        mockMvc.perform(MockMvcRequestBuilders.post("/solicitudes/cartas")
                        .header("Authorization", "Bearer " + token.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombreCarta\":\"Pikachu EX\",\"idJuego\":\"" + "id inexistente" + "\" }"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void usuarioNoPuedeCrearSolicitudCartaConExcesoCaracteres() throws Exception {
        LoginRequestDTO loginDTO = new LoginRequestDTO("usuarioTest",
                "password123");
        AuthResponse token = authService.login(loginDTO);

        Juego juego = juegoRepository.findOneByNombre("Pokemon");

        mockMvc.perform(MockMvcRequestBuilders.post("/solicitudes/cartas")
                        .header("Authorization", "Bearer " + token.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombreCarta\":\"Pikachu VMAX de la Tormenta Celestial con Relámpago Gigavoltio y Escudo de Trueno Definitivo GX Definitivo\",\"idJuego\":\"" + juego.getId() + "\" }"))
                .andExpect(status().isPayloadTooLarge());
    }

    @Test
    public void usuarioNoPuedeCrearMasDeTresSolicitudesCartaPorDia() throws Exception {
        LoginRequestDTO loginDTO = new LoginRequestDTO("usuarioTest",
                "password123");
        AuthResponse token = authService.login(loginDTO);

        Juego juego = juegoRepository.findOneByNombre("Pokemon");

        mockMvc.perform(MockMvcRequestBuilders.post("/solicitudes/cartas")
                        .header("Authorization", "Bearer " + token.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombreCarta\":\"Charizard EX\",\"idJuego\":\"" + juego.getId() + "\" }"))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.post("/solicitudes/cartas")
                        .header("Authorization", "Bearer " + token.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombreCarta\":\"Charizard EX\",\"idJuego\":\"" + juego.getId() + "\" }"))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.post("/solicitudes/cartas")
                        .header("Authorization", "Bearer " + token.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombreCarta\":\"Charizard EX\",\"idJuego\":\"" + juego.getId() + "\" }"))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.post("/solicitudes/cartas")
                        .header("Authorization", "Bearer " + token.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombreCarta\":\"Charizard EX\",\"idJuego\":\"" + juego.getId() + "\" }"))
                .andExpect(status().isTooManyRequests());
    }

    @Test
    public void usuarioYAdminPuedenAccederASolicitudCarta() throws Exception {
        LoginRequestDTO loginDTO = new LoginRequestDTO("usuarioTest",
                "password123");
        AuthResponse tokenUsuario1 = authService.login(loginDTO);

        LoginRequestDTO login2DTO = new LoginRequestDTO("adminTest",
                "password123");
        AuthResponse tokenAdmin = authService.login(login2DTO);

        Juego juego = juegoRepository.findOneByNombre("Pokemon");

        mockMvc.perform(MockMvcRequestBuilders.post("/solicitudes/cartas")
                        .header("Authorization", "Bearer " + tokenUsuario1.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombreCarta\":\"Charizard EX\",\"idJuego\":\"" + juego.getId() + "\" }"))
                .andExpect(status().isOk());

        String idSolicitud = solicitudCartaRepository.obtenerTodasSolicitudes().get(0).getId();

        mockMvc.perform(MockMvcRequestBuilders.get("/solicitudes/cartas/" + idSolicitud)
                        .header("Authorization", "Bearer " + tokenAdmin.token()))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/solicitudes/cartas/" + idSolicitud)
                        .header("Authorization", "Bearer " + tokenUsuario1.token()))
                .andExpect(status().isOk());
    }

    @Test
    public void usuarioExternoNoPuedeAccederASolicitudCarta() throws Exception {
        LoginRequestDTO loginDTO = new LoginRequestDTO("usuarioTest",
                "password123");
        AuthResponse tokenUsuario1 = authService.login(loginDTO);

        LoginRequestDTO login2DTO = new LoginRequestDTO("usuario2Test",
                "password123");
        AuthResponse tokenUsuario2 = authService.login(login2DTO);

        Juego juego = juegoRepository.findOneByNombre("Pokemon");

        mockMvc.perform(MockMvcRequestBuilders.post("/solicitudes/cartas")
                        .header("Authorization", "Bearer " + tokenUsuario1.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombreCarta\":\"Charizard EX\",\"idJuego\":\"" + juego.getId() + "\" }"))
                .andExpect(status().isOk());

        String idSolicitud = solicitudCartaRepository.obtenerTodasSolicitudes().get(0).getId();

        mockMvc.perform(MockMvcRequestBuilders.get("/solicitudes/cartas/" + idSolicitud)
                        .header("Authorization", "Bearer " + tokenUsuario2.token()))
                .andExpect(status().isForbidden());


    }

    @Test
    public void usuarioPuedeAccederATodasSusSolicitudesDeCarta() throws Exception {
        LoginRequestDTO loginDTO = new LoginRequestDTO("usuarioTest",
                "password123");
        AuthResponse tokenUsuario1 = authService.login(loginDTO);

        Juego juego = juegoRepository.findOneByNombre("Pokemon");

        mockMvc.perform(MockMvcRequestBuilders.post("/solicitudes/cartas")
                        .header("Authorization", "Bearer " + tokenUsuario1.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombreCarta\":\"Charizard EX\",\"idJuego\":\"" + juego.getId() + "\" }"))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.post("/solicitudes/cartas")
                        .header("Authorization", "Bearer " + tokenUsuario1.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombreCarta\":\"Charizard GX\",\"idJuego\":\"" + juego.getId() + "\" }"))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/solicitudes/cartas/persona")
                        .header("Authorization", "Bearer " + tokenUsuario1.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

    }

    @Test
    public void adminAceptaSolicitudCarta() throws Exception {
        LoginRequestDTO loginDTO = new LoginRequestDTO("usuarioTest",
                "password123");
        AuthResponse tokenUsuario1 = authService.login(loginDTO);

        LoginRequestDTO login2DTO = new LoginRequestDTO("adminTest",
                "password123");
        AuthResponse tokenAdmin = authService.login(login2DTO);

        Juego juego = juegoRepository.findOneByNombre("Pokemon");

        mockMvc.perform(MockMvcRequestBuilders.post("/solicitudes/cartas")
                        .header("Authorization", "Bearer " + tokenUsuario1.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombreCarta\":\"Charizard EX\",\"idJuego\":\"" + juego.getId() + "\" }"))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.post("/solicitudes/cartas/"+ solicitudCartaRepository.obtenerTodasSolicitudes().get(0).getId() +"/estado-aprobacion")
                        .header("Authorization", "Bearer " + tokenAdmin.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"APROBADA\"}"))
                .andExpect(status().isOk());

        assertEquals(EstadoSolicitud.APROBADA, solicitudCartaRepository.obtenerTodasSolicitudes().get(0).getEstado());

        List<Carta> cartas = cartaRepository.obtenerCartas();

        assertTrue(cartas.stream().anyMatch(cartaLista -> cartaLista.getNombre().equals("Charizard EX")));
    }

    @Test
    public void adminAceptaSolicitudCartaYSeRechazaAutomaticamenteOtra() throws Exception {
        LoginRequestDTO loginDTO = new LoginRequestDTO("usuarioTest",
                "password123");
        AuthResponse tokenUsuario1 = authService.login(loginDTO);

        LoginRequestDTO login2DTO = new LoginRequestDTO("adminTest",
                "password123");
        AuthResponse tokenAdmin = authService.login(login2DTO);

        Juego juego = juegoRepository.findOneByNombre("Pokemon");

        mockMvc.perform(MockMvcRequestBuilders.post("/solicitudes/cartas")
                        .header("Authorization", "Bearer " + tokenUsuario1.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombreCarta\":\"Charizard EX\",\"idJuego\":\"" + juego.getId() + "\" }"))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.post("/solicitudes/cartas")
                        .header("Authorization", "Bearer " + tokenUsuario1.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombreCarta\":\"Charizard EX\",\"idJuego\":\"" + juego.getId() + "\" }"))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.post("/solicitudes/cartas/"+ solicitudCartaRepository.obtenerTodasSolicitudes().get(0).getId() +"/estado-aprobacion")
                        .header("Authorization", "Bearer " + tokenAdmin.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"APROBADA\"}"))
                .andExpect(status().isOk());

        assertEquals(EstadoSolicitud.APROBADA, solicitudCartaRepository.obtenerTodasSolicitudes().get(0).getEstado());
        assertEquals(EstadoSolicitud.RECHAZADA, solicitudCartaRepository.obtenerTodasSolicitudes().get(1).getEstado());
    }

    @Test
    public void adminRechazaSolicitudCarta() throws Exception {
        LoginRequestDTO loginDTO = new LoginRequestDTO("usuarioTest",
                "password123");
        AuthResponse tokenUsuario1 = authService.login(loginDTO);

        LoginRequestDTO login2DTO = new LoginRequestDTO("adminTest",
                "password123");
        AuthResponse tokenAdmin = authService.login(login2DTO);

        Juego juego = juegoRepository.findOneByNombre("Pokemon");

        mockMvc.perform(MockMvcRequestBuilders.post("/solicitudes/cartas")
                        .header("Authorization", "Bearer " + tokenUsuario1.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombreCarta\":\"Charizard EX\",\"idJuego\":\"" + juego.getId() + "\" }"))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.post("/solicitudes/cartas/"+ solicitudCartaRepository.obtenerTodasSolicitudes().get(0).getId() +"/estado-aprobacion")
                        .header("Authorization", "Bearer " + tokenAdmin.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"RECHAZADA\"}"))
                .andExpect(status().isOk());

        assertEquals(EstadoSolicitud.RECHAZADA, solicitudCartaRepository.obtenerTodasSolicitudes().get(0).getEstado());
    }

    @Test
    public void adminIngresaMalEstadoCarta() throws Exception {
        LoginRequestDTO loginDTO = new LoginRequestDTO("usuarioTest",
                "password123");
        AuthResponse tokenUsuario1 = authService.login(loginDTO);

        LoginRequestDTO login2DTO = new LoginRequestDTO("adminTest",
                "password123");
        AuthResponse tokenAdmin = authService.login(login2DTO);

        Juego juego = juegoRepository.findOneByNombre("Pokemon");

        mockMvc.perform(MockMvcRequestBuilders.post("/solicitudes/cartas")
                        .header("Authorization", "Bearer " + tokenUsuario1.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombreCarta\":\"Charizard EX\",\"idJuego\":\"" + juego.getId() + "\" }"))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.post("/solicitudes/cartas/"+ solicitudCartaRepository.obtenerTodasSolicitudes().get(0).getId() +"/estado-aprobacion")
                        .header("Authorization", "Bearer " + tokenAdmin.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"ESTADO ERRONEO\"}"))
                .andExpect(status().isBadRequest());

    }
}
