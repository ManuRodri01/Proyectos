package api.Service;


import api.DTO.AuthResponse;
import api.DTO.LoginRequestDTO;
import api.DTO.RegisterRequestDTO;
import api.Entity.Persona;
import api.Entity.RolesPersona;
import api.Repository.PersonaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
public class AuthServiceTest {


    @Autowired
    private AuthService authService;

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @DynamicPropertySource
    static void overrideMongoProps(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeEach
    void setUp() {
        if (personaRepository.findByNombre("usuarioTest").isEmpty()) {
            Persona persona = new Persona("usuarioTest",
                    passwordEncoder.encode("password123"),
                    RolesPersona.USUARIO);

            personaRepository.guardarPersona(persona);
        }
    }

    @Test
    void loginDevuelveToken() {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO(
                "usuarioTest",
                "password123");
        AuthResponse response = authService.login(loginRequestDTO);
        assertNotNull(response.token());
        assertFalse(response.token().isEmpty());
    }

    @Test
    void loginContraseniaIncorrectaLanzaExcepcion() {
        assertThrows(Exception.class, () -> {
            LoginRequestDTO loginRequestDTO = new LoginRequestDTO(
                    "usuarioTest",
                    "contraseniaIncorrecta");
            authService.login(loginRequestDTO);
        });
    }

    @Test
    void loginConUsuarioInexistenteLanzaExcepcion() {
        assertThrows(Exception.class, () -> {
            LoginRequestDTO loginRequestDTO = new LoginRequestDTO(
                    "usuarioNoExiste",
                    "password123");
            authService.login(loginRequestDTO);
        });
    }

    @Test
    void registerCreaUsuario(){
        RegisterRequestDTO registerRequestDTO = new RegisterRequestDTO("usuarioRegister", "contrasenia", RolesPersona.USUARIO);
        AuthResponse response = authService.register(registerRequestDTO);
        assertTrue(personaRepository.findByNombre("usuarioRegister").isPresent());
        Optional<Persona> personaOptional = personaRepository.findByNombre("usuarioRegister");
        assertTrue(personaOptional.isPresent());
        Persona persona = personaOptional.get();

        assertEquals(persona.getNombre(), "usuarioRegister");
        assertTrue(passwordEncoder.matches("contrasenia", persona.getPassword()));
    }

    @Test
    void registerConUsuarioExistenteLanzaExcepcion() {
        assertThrows(Exception.class, () -> {
            RegisterRequestDTO registerRequestDTO = new RegisterRequestDTO(
                    "usuarioTest",
                    "password123",
                    RolesPersona.USUARIO);
            authService.register(registerRequestDTO);
        });
    }
}