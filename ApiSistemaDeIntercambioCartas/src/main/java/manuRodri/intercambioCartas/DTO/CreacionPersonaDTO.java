package manuRodri.intercambioCartas.DTO;

import manuRodri.intercambioCartas.Entity.rolesPersona;
import jakarta.validation.constraints.NotBlank;

public record CreacionPersonaDTO(
        @NotBlank
        String nombre,
        @NotBlank
        String contrasenia,
        rolesPersona rol
) {
}
