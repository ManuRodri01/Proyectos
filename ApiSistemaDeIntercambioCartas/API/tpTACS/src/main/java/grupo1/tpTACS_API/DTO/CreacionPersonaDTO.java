package grupo1.tpTACS_API.DTO;


import grupo1.tpTACS_API.Entity.RolesPersona;
import jakarta.validation.constraints.NotBlank;

public record CreacionPersonaDTO(
        @NotBlank
        String nombre,
        @NotBlank
        String contrasenia,
        RolesPersona rol
) {
}
