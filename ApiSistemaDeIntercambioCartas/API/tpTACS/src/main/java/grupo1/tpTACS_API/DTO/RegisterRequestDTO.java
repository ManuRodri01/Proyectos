package grupo1.tpTACS_API.DTO;


import grupo1.tpTACS_API.Entity.RolesPersona;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequestDTO(
        @NotBlank
        String nombreUsuario,
        @NotBlank
        String contrasenia,
        RolesPersona rolesPersona
) {
}
