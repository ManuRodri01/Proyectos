package grupo1.tpTACS_API.Service;


import grupo1.tpTACS_API.DTO.AuthResponse;
import grupo1.tpTACS_API.DTO.LoginRequestDTO;
import grupo1.tpTACS_API.DTO.RegisterRequestDTO;
import grupo1.tpTACS_API.Entity.Persona;
import grupo1.tpTACS_API.Entity.RolesPersona;
import grupo1.tpTACS_API.Repository.PersonaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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