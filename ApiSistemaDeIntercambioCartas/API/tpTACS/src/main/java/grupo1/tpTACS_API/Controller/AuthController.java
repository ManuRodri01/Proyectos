package grupo1.tpTACS_API.Controller;


import grupo1.tpTACS_API.DTO.AuthResponse;
import grupo1.tpTACS_API.DTO.LoginRequestDTO;
import grupo1.tpTACS_API.DTO.RegisterRequestDTO;
import grupo1.tpTACS_API.Service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import grupo1.tpTACS_API.Entity.Persona;


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
