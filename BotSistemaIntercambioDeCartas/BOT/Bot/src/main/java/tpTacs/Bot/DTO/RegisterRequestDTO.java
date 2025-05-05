package tpTacs.Bot.DTO;

import jakarta.validation.constraints.NotBlank;
import tpTacs.Bot.RolesPersona;

public record RegisterRequestDTO(
        @NotBlank
        String nombreUsuario,
        @NotBlank
        String contrasenia,
        RolesPersona rolesPersona
) {
}
