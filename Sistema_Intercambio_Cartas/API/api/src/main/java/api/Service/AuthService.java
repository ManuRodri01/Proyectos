package api.Service;


import api.DTO.AuthResponse;
import api.DTO.LoginRequestDTO;
import api.DTO.RegisterRequestDTO;
import api.Entity.Persona;
import api.Exception.ExcesoDeCaracteresException;
import api.Exception.PersonaNoEncontradaException;
import api.Exception.UsuarioYaExisteException;
import api.JWT.JwtService;
import api.Repository.PersonaRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final PersonaRepository personaRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(PersonaRepository personaRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.personaRepository = personaRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Utiliza el authenticationManager para auntenticar al usuario con su nombre de
     * usuario y contraseña. En caso de ser exitosa, genera un token JWT con la
     * información del usuario.
     * @param loginRequest DTO que contiene el nombre de usuario y contrasela
     * @return un AuthResponse donde se almacena el token JWT
     * @throws AuthenticationException en caso de que las credenciales no sean válidas
     * @throws PersonaNoEncontradaException en caso de que la persona no se encuentre en la base de datos
     */
    public AuthResponse login(LoginRequestDTO loginRequest) {
        if(loginRequest.nombreUsuario().length() > 30){
            throw new ExcesoDeCaracteresException("Nombre de Usuario", 30);
        }
        if(loginRequest.contrasenia().length() > 50){
            throw new ExcesoDeCaracteresException("Contraseña", 50);
        }
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.nombreUsuario(), loginRequest.contrasenia()));

        Persona userDetails = personaRepository.findByNombre(loginRequest.nombreUsuario()).get();

        return new AuthResponse(jwtService.generarToken(userDetails), userDetails.getRol());
    }

    /**
     * Registra un nuevo usuario en el sistema, validando que no exista otro usuario
     * con el mismo nombre. En caso de que no exista, crea una entidad persona y la guarda
     * en la base de datos, encriptando la contraseña
     * @param registerRequest DTO que contiene el nombre de usuario, la contraseña y el rol de la persona
     * @return un AuthResponse donde se almacena el token JWT
     * @throws UsuarioYaExisteException en caso de que el usuario que se intenta registrar ya exista
     */
    public AuthResponse register(RegisterRequestDTO registerRequest) {
        if(personaRepository.findByNombre(registerRequest.nombreUsuario()).isPresent()) {
            throw new UsuarioYaExisteException(registerRequest.nombreUsuario());
        }
        Persona persona = new Persona(registerRequest.nombreUsuario(),
                passwordEncoder.encode(registerRequest.contrasenia()),
                registerRequest.rolesPersona());

        personaRepository.guardarPersona(persona);

        return new AuthResponse(jwtService.generarToken(persona), persona.getRol());
    }
}
