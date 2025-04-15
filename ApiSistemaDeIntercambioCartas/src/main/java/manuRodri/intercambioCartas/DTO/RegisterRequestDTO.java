package manuRodri.intercambioCartas.DTO;

import manuRodri.intercambioCartas.Entity.rolesPersona;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequestDTO(
        @NotBlank
        String nombreUsuario,
        @NotBlank
        String contrasenia,
        rolesPersona rolesPersona
) {
}
