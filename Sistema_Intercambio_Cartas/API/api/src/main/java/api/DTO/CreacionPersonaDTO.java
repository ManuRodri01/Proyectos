package api.DTO;


import api.Entity.RolesPersona;
import jakarta.validation.constraints.NotBlank;

public record CreacionPersonaDTO(
        @NotBlank
        String nombre,
        @NotBlank
        String contrasenia,
        RolesPersona rol
) {
}
