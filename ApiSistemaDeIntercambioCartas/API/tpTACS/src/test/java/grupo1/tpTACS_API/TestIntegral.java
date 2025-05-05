package grupo1.tpTACS_API;


import grupo1.tpTACS_API.DTO.AuthResponse;
import grupo1.tpTACS_API.DTO.RegisterRequestDTO;
import grupo1.tpTACS_API.Entity.RolesPersona;
import grupo1.tpTACS_API.Service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TestIntegral {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthService authService;

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

        mockMvc.perform(MockMvcRequestBuilders.get("/juegos/0")
                        .header("Authorization", "Bearer " + authAdmin.token()))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.nombre").value("Pokemon"));

        mockMvc.perform(MockMvcRequestBuilders.post("/cartas")
                        .header("Authorization", "Bearer " + authAdmin.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Pikachu\",\"idJuego\":0}"))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/cartas/0")
                        .header("Authorization", "Bearer " + authAdmin.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreCarta").value("Pikachu"));

        //Un usuario crea una publicacion y se asegura que la publicacion se haya subido

        mockMvc.perform(MockMvcRequestBuilders.post("/publicaciones")
                        .header("Authorization", "Bearer " + authPublicador.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cartaId\":0,\"descripcion\":\"Carta de Pikachu\",\"intereses\":[],\"imagenes\":[], \"precio\":400, \"estadoConservacion\":\"NUEVA\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/publicaciones/0")
                        .header("Authorization", "Bearer " + authOfertante.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descripcion").value("Carta de Pikachu"));

        //Otro usuario distinto crea una oferta para la publicacion publicada

        mockMvc.perform(MockMvcRequestBuilders.post("/ofertas")
                        .header("Authorization", "Bearer " + authOfertante.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"publicacionId\":0,\"cartas\":[],\"valor\":350}"))
                .andExpect(status().isOk());

        //El publicador y el ofertante pueden acceder a la oferta sin problemas

        mockMvc.perform(MockMvcRequestBuilders.get("/ofertas/0")
                        .header("Authorization", "Bearer " + authOfertante.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valor").value(350));

        mockMvc.perform(MockMvcRequestBuilders.get("/ofertas/0")
                        .header("Authorization", "Bearer " + authPublicador.token()))
                .andExpect(status().isOk());

        //Otro usuario intenta acceder a la oferta, pero es rechazado

        mockMvc.perform(MockMvcRequestBuilders.get("/ofertas/0")
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
                        .content("{\"publicacionId\":0,\"cartas\":[],\"valor\":300}"))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/ofertas/1")
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

        mockMvc.perform(MockMvcRequestBuilders.post("/ofertas/0/aceptar")
                        .header("Authorization", "Bearer " + authPublicador.token()))
                .andExpect(status().isOk());

        //La primera oferta figura como aceptada

        mockMvc.perform(MockMvcRequestBuilders.get("/ofertas/0")
                        .header("Authorization", "Bearer " + authOfertante.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoOferta").value("ACEPTADA"));

        //La segunda oferta figura como rechazada

        mockMvc.perform(MockMvcRequestBuilders.get("/ofertas/1")
                        .header("Authorization", "Bearer " + authOfertante2.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoOferta").value("RECHAZADA"));

    }

}
