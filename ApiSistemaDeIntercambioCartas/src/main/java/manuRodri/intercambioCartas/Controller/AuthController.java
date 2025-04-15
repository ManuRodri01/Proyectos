package manuRodri.intercambioCartas.Controller;

import manuRodri.intercambioCartas.DTO.AuthResponse;
import manuRodri.intercambioCartas.DTO.LoginRequestDTO;
import manuRodri.intercambioCartas.DTO.RegisterRequestDTO;
import manuRodri.intercambioCartas.Service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Autenticacion")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;

    }

    @Operation(summary = "Permite a un usuario autenticarse en el sistema")
    @PostMapping("/auth/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequestDTO loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @Operation(summary = "Permite a un usuario registrarse en el sistema")
    @PostMapping("/auth/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequestDTO registerRequest) {
        return ResponseEntity.ok(authService.register(registerRequest));
    }
}
