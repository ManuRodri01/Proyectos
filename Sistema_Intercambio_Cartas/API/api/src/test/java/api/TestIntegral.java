package api;


import api.DTO.AuthResponse;
import api.DTO.RegisterRequestDTO;
import api.Entity.*;
import api.Repository.*;
import api.Service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
public class TestIntegral {

    @Autowired
    private MockMvc mockMvc;

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");

    @Autowired
    private AuthService authService;

    @Autowired
    private JuegoRepository juegoRepository;

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private CartaRepository cartaRepository;

    @Autowired
    private PublicacionRepository publicacionRepository;

    @Autowired
    private OfertaRepository ofertaRepository;

    @Autowired
    private SolicitudCartaRepository solicitudCartaRepository;

    @Autowired
    private SolicitudJuegoRepository solicitudJuegoRepository;

    @DynamicPropertySource
    static void overrideMongoProps(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Test
    @DisplayName("Test de funcionamiento general de la Api, Admin crea juegos y cartas, usuarios publican, hacen ofertas, aceptan ofertas y se rechazan ofertas")
    void TestFuncionamientoIntegral() throws Exception {
        RegisterRequestDTO registro1 = new RegisterRequestDTO(
                "publicadorTest",
                "password123",
                RolesPersona.USUARIO);
        AuthResponse authPublicador = authService.register(registro1);

        RegisterRequestDTO registro2 = new RegisterRequestDTO(
                "OfertanteTest",
                "password123",
                RolesPersona.USUARIO);
        AuthResponse authOfertante = authService.register(registro2);

        RegisterRequestDTO registro3 = new RegisterRequestDTO(
                "Ofertante2",
                "password123",
                RolesPersona.USUARIO);
        AuthResponse authOfertante2 = authService.register(registro3);

        RegisterRequestDTO registro4 = new RegisterRequestDTO(
                "adminTest",
                "password123",
                RolesPersona.ADMIN);
        AuthResponse authAdmin = authService.register(registro4);

        //El Admin crea juego y cartas

        mockMvc.perform(MockMvcRequestBuilders.post("/juegos")
                        .header("Authorization", "Bearer " + authAdmin.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Pokemon\"}"))
                .andExpect(status().isOk());

        Juego juego = juegoRepository.findOneByNombre("Pokemon");
        String idJuego = juego.getId();

        mockMvc.perform(MockMvcRequestBuilders.get("/juegos/" + juego.getId())
                        .header("Authorization", "Bearer " + authAdmin.token()))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.nombre").value("Pokemon"));

        mockMvc.perform(MockMvcRequestBuilders.post("/cartas")
                        .header("Authorization", "Bearer " + authAdmin.token())
                        .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"Pikachu\",\"idJuego\":\"" + idJuego + "\"}"))
                .andExpect(status().isOk());

        List<Carta> carta = cartaRepository.getCartasByJuego(juego.getId());

        mockMvc.perform(MockMvcRequestBuilders.get("/cartas/" + carta.get(0).getId())
                        .header("Authorization", "Bearer " + authAdmin.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreCarta").value("Pikachu"));

        //Un usuario crea una publicacion y se asegura que la publicacion se haya subido

        mockMvc.perform(MockMvcRequestBuilders.post("/publicaciones")
                        .header("Authorization", "Bearer " + authPublicador.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cartaId\":\"" + carta.get(0).getId() + "\",\"titulo\":\"Carta de Pikachu\",\"descripcion\":\"Carta de Pikachu\",\"intereses\":[],\"imagenes\":[], \"precio\":400, \"estadoConservacion\":\"NUEVA\"}"))
                .andExpect(status().isOk());

        List<Publicacion> publicacion = publicacionRepository.getAllPublicaciones();

        mockMvc.perform(MockMvcRequestBuilders.get("/publicaciones/" + publicacion.get(0).getId())
                        .header("Authorization", "Bearer " + authOfertante.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descripcion").value("Carta de Pikachu"));

        //Otro usuario distinto crea una oferta para la publicacion publicada

        mockMvc.perform(MockMvcRequestBuilders.post("/ofertas")
                        .header("Authorization", "Bearer " + authOfertante.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"publicacionId\":\"" + publicacion.get(0).getId() + "\",\"cartas\":[],\"valor\":350}"))
                .andExpect(status().isOk());

        //El publicador y el ofertante pueden acceder a la oferta sin problemas

        List<Oferta> oferta = ofertaRepository.getOfertasByPublicacion(publicacion.get(0).getId());

        mockMvc.perform(MockMvcRequestBuilders.get("/ofertas/" + oferta.get(0).getId())
                        .header("Authorization", "Bearer " + authOfertante.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valor").value(350));

        mockMvc.perform(MockMvcRequestBuilders.get("/ofertas/" + oferta.get(0).getId())
                        .header("Authorization", "Bearer " + authPublicador.token()))
                .andExpect(status().isOk());

        //Otro usuario intenta acceder a la oferta, pero es rechazado

        mockMvc.perform(MockMvcRequestBuilders.get("/ofertas/" + oferta.get(0).getId())
                        .header("Authorization", "Bearer " + authOfertante2.token()))
                .andExpect(status().isForbidden());

        //El primer ofertante ve desde su perfil las ofertas que realizo

        mockMvc.perform(MockMvcRequestBuilders.get("/personas/ofertas-hechas")
                        .header("Authorization", "Bearer " + authOfertante.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreCarta").value("Pikachu"))
                .andExpect(jsonPath("$[0].estado").value("EN_ESPERA"));

        //Un segundo usuario realiza una oferta para esa publicacion

        mockMvc.perform(MockMvcRequestBuilders.post("/ofertas")
                        .header("Authorization", "Bearer " + authOfertante2.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"publicacionId\":\"" + publicacion.get(0).getId() + "\",\"cartas\":[],\"valor\":300}"))
                .andExpect(status().isOk());

        List<Oferta> oferta2 = ofertaRepository.getOfertasByPublicacion(publicacion.get(0).getId());

        mockMvc.perform(MockMvcRequestBuilders.get("/ofertas/" + oferta2.get(1).getId())
                        .header("Authorization", "Bearer " + authOfertante2.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valor").value(300));

        //El publicador ve las ofertas que recibio desde su perfil

        mockMvc.perform(MockMvcRequestBuilders.get("/personas/ofertas-recibidas")
                        .header("Authorization", "Bearer " + authPublicador.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreCarta").value("Pikachu"))
                .andExpect(jsonPath("$[0].estado").value("EN_ESPERA"))
                .andExpect(jsonPath("$[0].nombreOfertante").value("OfertanteTest"))
                .andExpect(jsonPath("$[1].nombreOfertante").value("Ofertante2"));

        //El publicador acepta la primera oferta, rechazándose automaticamente todas las otras

        mockMvc.perform(MockMvcRequestBuilders.post("/ofertas/"+ oferta.get(0).getId() +"/aceptar")
                        .header("Authorization", "Bearer " + authPublicador.token()))
                .andExpect(status().isOk());

        //La primera oferta figura como aceptada

        mockMvc.perform(MockMvcRequestBuilders.get("/ofertas/" + oferta.get(0).getId())
                        .header("Authorization", "Bearer " + authOfertante.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoOferta").value("ACEPTADA"));

        //La segunda oferta figura como rechazada

        mockMvc.perform(MockMvcRequestBuilders.get("/ofertas/" + oferta2.get(1).getId())
                        .header("Authorization", "Bearer " + authOfertante2.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoOferta").value("RECHAZADA"));






        //Se crea solicitud de creacion de Juego

        mockMvc.perform(MockMvcRequestBuilders.post("/solicitudes/juegos")
                        .header("Authorization", "Bearer " + authOfertante2.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombreJuego\":\"Digimon\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/solicitudes/juegos")
                        .header("Authorization", "Bearer " + authAdmin.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreJuego").value("Digimon"))
                .andExpect(jsonPath("$[0].estado").value("EN_ESPERA"));

        //Se crea una segunda solicitud

        mockMvc.perform(MockMvcRequestBuilders.post("/solicitudes/juegos")
                        .header("Authorization", "Bearer " + authOfertante2.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombreJuego\":\"Digimon\"}"))
                .andExpect(status().isOk());

        //Se acepta la primera y la segunda deberia figurar rechazada

        List<SolicitudCreacionJuego> solicitudesJuego = solicitudJuegoRepository.obtenerTodasSolicitudes();

        mockMvc.perform(MockMvcRequestBuilders.post("/solicitudes/juegos/"+ solicitudesJuego.get(0).getId() +"/estado-aprobacion")
                        .header("Authorization", "Bearer " + authAdmin.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"APROBADA\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/solicitudes/juegos")
                        .header("Authorization", "Bearer " + authAdmin.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreJuego").value("Digimon"))
                .andExpect(jsonPath("$[0].estado").value("APROBADA"))
                .andExpect(jsonPath("$[1].estado").value("RECHAZADA"));

        //Usuario no admin no puede administrar una solicitud

        mockMvc.perform(MockMvcRequestBuilders.post("/solicitudes/juegos")
                        .header("Authorization", "Bearer " + authOfertante.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombreJuego\":\"Dragon Ball\"}"))
                .andExpect(status().isOk());

        solicitudesJuego = solicitudJuegoRepository.obtenerTodasSolicitudesEnEspera();

        mockMvc.perform(MockMvcRequestBuilders.post("/solicitudes/juegos/"+ solicitudesJuego.get(0).getId() +"/estado-aprobacion")
                        .header("Authorization", "Bearer " + authOfertante2.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"RECHAZADA\"}"))
                .andExpect(status().isForbidden());

        //Se rechaza una Solicitud

        mockMvc.perform(MockMvcRequestBuilders.post("/solicitudes/juegos/"+ solicitudesJuego.get(0).getId() +"/estado-aprobacion")
                        .header("Authorization", "Bearer " + authAdmin.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"RECHAZADA\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/solicitudes/juegos")
                        .header("Authorization", "Bearer " + authAdmin.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[2].nombreJuego").value("Dragon Ball"))
                .andExpect(jsonPath("$[2].estado").value("RECHAZADA"));

        //El publicador y el ofertante pueden acceder a la oferta sin problemas

        mockMvc.perform(MockMvcRequestBuilders.get("/solicitudes/juegos/" + solicitudesJuego.get(0).getId())
                        .header("Authorization", "Bearer " + authOfertante.token()))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/solicitudes/juegos/" + solicitudesJuego.get(0).getId())
                        .header("Authorization", "Bearer " + authAdmin.token()))
                .andExpect(status().isOk());

        //Otro usuario no puede acceder a la solicitud

        mockMvc.perform(MockMvcRequestBuilders.get("/solicitudes/juegos/" + solicitudesJuego.get(0).getId())
                        .header("Authorization", "Bearer " + authOfertante2.token()))
                .andExpect(status().isForbidden());

        //Usuario puede obtener todas las solicitudes de si mismo

        mockMvc.perform(MockMvcRequestBuilders.get("/solicitudes/juegos/persona")
                        .header("Authorization", "Bearer " + authOfertante2.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));


        //Usuario no admin no puede obtener todas las solicitudes

        mockMvc.perform(MockMvcRequestBuilders.get("/solicitudes/juegos")
                        .header("Authorization", "Bearer " + authOfertante2.token()))
                .andExpect(status().isForbidden());

        //Usuario no admin no puede obtener todas las solicitudes en espera

        mockMvc.perform(MockMvcRequestBuilders.get("/solicitudes/juegos/en_espera")
                        .header("Authorization", "Bearer " + authOfertante2.token()))
                .andExpect(status().isForbidden());






        //Se crea solicitud de creacion de Carta

        mockMvc.perform(MockMvcRequestBuilders.post("/solicitudes/cartas")
                        .header("Authorization", "Bearer " + authOfertante2.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombreCarta\":\"Pikachu EX\",\"idJuego\":\"" + idJuego + "\" }"))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/solicitudes/cartas")
                        .header("Authorization", "Bearer " + authAdmin.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreCarta").value("Pikachu EX"))
                .andExpect(jsonPath("$[0].estado").value("EN_ESPERA"))
                .andExpect(jsonPath("$[0].nombreJuego").value("Pokemon"));

        //Se crea una segunda solicitud

        mockMvc.perform(MockMvcRequestBuilders.post("/solicitudes/cartas")
                        .header("Authorization", "Bearer " + authOfertante2.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombreCarta\":\"Pikachu EX\",\"idJuego\":\"" + idJuego + "\" }"))
                .andExpect(status().isOk());

        //Se acepta la primera y la segunda deberia figurar rechazada

        List<SolicitudCreacionCarta> solicitudesCarta = solicitudCartaRepository.obtenerTodasSolicitudes();

        mockMvc.perform(MockMvcRequestBuilders.post("/solicitudes/cartas/"+ solicitudesCarta.get(0).getId() +"/estado-aprobacion")
                        .header("Authorization", "Bearer " + authAdmin.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"APROBADA\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/solicitudes/cartas")
                        .header("Authorization", "Bearer " + authAdmin.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreCarta").value("Pikachu EX"))
                .andExpect(jsonPath("$[0].estado").value("APROBADA"))
                .andExpect(jsonPath("$[1].estado").value("RECHAZADA"));

        //Usuario no admin no puede administrar una solicitud

        mockMvc.perform(MockMvcRequestBuilders.post("/solicitudes/cartas")
                        .header("Authorization", "Bearer " + authOfertante.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombreCarta\":\"Dragapult EX\",\"idJuego\":\"" + idJuego + "\" }"))
                .andExpect(status().isOk());

        solicitudesCarta = solicitudCartaRepository.obtenerTodasSolicitudesEnEspera();

        mockMvc.perform(MockMvcRequestBuilders.post("/solicitudes/cartas/"+ solicitudesCarta.get(0).getId() +"/estado-aprobacion")
                        .header("Authorization", "Bearer " + authOfertante2.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"RECHAZADA\"}"))
                .andExpect(status().isForbidden());

        //Se rechaza una Solicitud

        mockMvc.perform(MockMvcRequestBuilders.post("/solicitudes/cartas/"+ solicitudesCarta.get(0).getId() +"/estado-aprobacion")
                        .header("Authorization", "Bearer " + authAdmin.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"RECHAZADA\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/solicitudes/cartas")
                        .header("Authorization", "Bearer " + authAdmin.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[2].nombreCarta").value("Dragapult EX"))
                .andExpect(jsonPath("$[2].estado").value("RECHAZADA"));

        //El publicador y el ofertante pueden acceder a la oferta sin problemas

        mockMvc.perform(MockMvcRequestBuilders.get("/solicitudes/cartas/" + solicitudesCarta.get(0).getId())
                        .header("Authorization", "Bearer " + authOfertante.token()))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/solicitudes/cartas/" + solicitudesCarta.get(0).getId())
                        .header("Authorization", "Bearer " + authAdmin.token()))
                .andExpect(status().isOk());

        //Otro usuario no puede acceder a la solicitud

        mockMvc.perform(MockMvcRequestBuilders.get("/solicitudes/cartas/" + solicitudesCarta.get(0).getId())
                        .header("Authorization", "Bearer " + authOfertante2.token()))
                .andExpect(status().isForbidden());

        //Usuario puede ver todas las solicitudes de Cartas hechas

        mockMvc.perform(MockMvcRequestBuilders.get("/solicitudes/cartas/persona")
                        .header("Authorization", "Bearer " + authOfertante2.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        //Usuario no admin no puede obtener todas las solicitudes

        mockMvc.perform(MockMvcRequestBuilders.get("/solicitudes/cartas")
                        .header("Authorization", "Bearer " + authOfertante2.token()))
                .andExpect(status().isForbidden());

        //Usuario no admin no puede obtener todas las solicitudes en espera

        mockMvc.perform(MockMvcRequestBuilders.get("/solicitudes/cartas/en_espera")
                        .header("Authorization", "Bearer " + authOfertante2.token()))
                .andExpect(status().isForbidden());

        //El juego y la carta aceptados son creados

        List<Juego> juegos = juegoRepository.getTodos();

        assertTrue(juegos.stream().anyMatch(juegoLista -> juegoLista.getNombre().equals("Digimon")));

        List<Carta> cartas = cartaRepository.obtenerCartas();

        assertTrue(cartas.stream().anyMatch(cartaLista -> cartaLista.getNombre().equals("Pikachu EX")));

    }



}
